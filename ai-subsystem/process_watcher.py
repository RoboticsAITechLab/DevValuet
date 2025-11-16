"""Simple process watcher that looks for suspicious activity and calls the AI kill-switch.

This is a small scaffold intended to run alongside the AI FastAPI app or as a separate process
on the same host. It uses psutil to sample process list and will call the AI subsystem's
/ai/disable endpoint when a simple policy is triggered (e.g. an unknown process writes to
an important directory or a process consumes excessive CPU for a sustained period).

This file is intentionally small and conservative — extend rules and secure the HTTP calls
with authentication in production.
"""
import time
import logging
from typing import Set

import psutil
import requests

LOG = logging.getLogger("devvault.ai.process_watcher")
logging.basicConfig(level=logging.INFO)

# Config — tweak as needed or read from env/config file
AI_SUBSYSTEM_URL = "http://localhost:8000"
CHECK_INTERVAL_SECONDS = 5
CPU_THRESHOLD_PERCENT = 80.0
SUSTAINED_CYCLES = 3


def call_disable(timeout_seconds: int = 60) -> bool:
    try:
        url = f"{AI_SUBSYSTEM_URL}/ai/disable"
        LOG.info("Calling AI disable endpoint: %s (timeout=%s)", url, timeout_seconds)
        resp = requests.post(url, json={"timeout_seconds": timeout_seconds}, timeout=5)
        resp.raise_for_status()
        LOG.info("AI disable response: %s", resp.json())
        return True
    except Exception as ex:
        LOG.exception("Failed to call AI disable: %s", ex)
        return False


def monitor():
    # Track processes that exceed CPU threshold across cycles
    sustained: dict = {}

    while True:
        try:
            # snapshot processes
            for proc in psutil.process_iter(['pid', 'name', 'cpu_percent', 'exe', 'cmdline']):
                info = proc.info
                cpu = info.get('cpu_percent') or 0.0
                pid = info.get('pid')
                name = info.get('name') or ''

                # If CPU high, increment counter
                if cpu >= CPU_THRESHOLD_PERCENT:
                    sustained[pid] = sustained.get(pid, 0) + 1
                    LOG.debug("High CPU: pid=%s name=%s cpu=%s count=%s", pid, name, cpu, sustained[pid])
                    if sustained[pid] >= SUSTAINED_CYCLES:
                        LOG.warning("Process %s (pid=%s) exceeded CPU threshold for %s cycles; triggering AI disable", name, pid, sustained[pid])
                        call_disable(timeout_seconds=300)
                        sustained.pop(pid, None)
                else:
                    # decay counter
                    if pid in sustained:
                        sustained[pid] = max(0, sustained[pid] - 1)
                        if sustained[pid] == 0:
                            sustained.pop(pid, None)

            # Rotate/trim sustained to only include currently running pids
            running_pids: Set[int] = {p.pid for p in psutil.process_iter()} 
            for pid in list(sustained.keys()):
                if pid not in running_pids:
                    sustained.pop(pid, None)

        except Exception:
            LOG.exception("Error while monitoring processes")

        time.sleep(CHECK_INTERVAL_SECONDS)


if __name__ == '__main__':
    LOG.info("Starting DevVault process watcher (AI guard). AI URL=%s", AI_SUBSYSTEM_URL)
    monitor()
