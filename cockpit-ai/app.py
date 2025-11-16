from fastapi import FastAPI, HTTPException, Depends, Request, Response, BackgroundTasks
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from pydantic import BaseModel, Field
import os
import asyncio
import httpx
import time
import json
import hashlib
import secrets
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Any
from collections import defaultdict, deque
import logging
import jwt
from contextlib import asynccontextmanager

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Advanced AI Gateway Configuration
class AIGatewayConfig:
    AI_SUBSYSTEM_URL = "http://localhost:8000"  # AI-Subsystem endpoint
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY", "devvault-ai-gateway-2025")
    JWT_ALGORITHM = "HS256"
    RATE_LIMIT_WINDOW = 60  # seconds
    RATE_LIMIT_MAX_REQUESTS = 100  # per window
    HEALTH_CHECK_INTERVAL = 30  # seconds
    AI_TIMEOUT = 60  # seconds
    MAX_CONCURRENT_REQUESTS = 50
    ENABLE_ANALYTICS = True
    
config = AIGatewayConfig()

# Passive mode by default
AI_DISABLED_FILE = "./.aegis_disabled"
GATEWAY_STATS_FILE = "./gateway_stats.json"

def is_disabled():
    return os.path.exists(AI_DISABLED_FILE)

# Advanced Data Models
class DisableRequest(BaseModel):
    disable: bool
    reason: Optional[str] = None

class AIRequest(BaseModel):
    operation: str = Field(..., description="AI operation to perform")
    data: Dict[str, Any] = Field(default_factory=dict)
    priority: int = Field(default=5, ge=1, le=10)
    metadata: Optional[Dict[str, Any]] = None

class AuthRequest(BaseModel):
    username: str
    password: str

class APIKeyRequest(BaseModel):
    name: str
    permissions: List[str] = ["ai:read", "ai:write"]
    expires_in_hours: int = 24

# Advanced Gateway Features
class SecurityManager:
    def __init__(self):
        self.api_keys = {}
        self.sessions = {}
        self.load_keys()
    
    def generate_api_key(self, name: str, permissions: List[str], expires_in_hours: int) -> str:
        api_key = f"ak_{secrets.token_urlsafe(32)}"
        expiry = datetime.now() + timedelta(hours=expires_in_hours)
        
        self.api_keys[api_key] = {
            "name": name,
            "permissions": permissions,
            "created": datetime.now().isoformat(),
            "expires": expiry.isoformat(),
            "usage_count": 0
        }
        self.save_keys()
        return api_key
    
    def validate_api_key(self, api_key: str) -> Optional[Dict]:
        if api_key in self.api_keys:
            key_data = self.api_keys[api_key]
            if datetime.fromisoformat(key_data["expires"]) > datetime.now():
                key_data["usage_count"] += 1
                return key_data
            else:
                del self.api_keys[api_key]
                self.save_keys()
        return None
    
    def save_keys(self):
        try:
            with open("api_keys.json", "w") as f:
                json.dump(self.api_keys, f, indent=2)
        except Exception as e:
            logger.error(f"Failed to save API keys: {e}")
    
    def load_keys(self):
        try:
            if os.path.exists("api_keys.json"):
                with open("api_keys.json", "r") as f:
                    self.api_keys = json.load(f)
        except Exception as e:
            logger.error(f"Failed to load API keys: {e}")

class RateLimiter:
    def __init__(self):
        self.requests = defaultdict(deque)
    
    def is_allowed(self, client_id: str) -> bool:
        now = time.time()
        window_start = now - config.RATE_LIMIT_WINDOW
        
        # Clean old requests
        while (self.requests[client_id] and 
               self.requests[client_id][0] < window_start):
            self.requests[client_id].popleft()
        
        # Check if within limit
        if len(self.requests[client_id]) < config.RATE_LIMIT_MAX_REQUESTS:
            self.requests[client_id].append(now)
            return True
        
        return False

class AIMonitor:
    def __init__(self):
        self.stats = {
            "total_requests": 0,
            "successful_requests": 0,
            "failed_requests": 0,
            "average_response_time": 0,
            "ai_subsystem_health": "unknown",
            "uptime_start": datetime.now().isoformat(),
            "last_health_check": None
        }
        self.load_stats()
        self.response_times = deque(maxlen=100)
    
    def record_request(self, success: bool, response_time: float):
        self.stats["total_requests"] += 1
        if success:
            self.stats["successful_requests"] += 1
        else:
            self.stats["failed_requests"] += 1
        
        self.response_times.append(response_time)
        if self.response_times:
            self.stats["average_response_time"] = sum(self.response_times) / len(self.response_times)
        
        self.save_stats()
    
    async def health_check(self):
        try:
            async with httpx.AsyncClient(timeout=10) as client:
                response = await client.get(f"{config.AI_SUBSYSTEM_URL}/health")
                if response.status_code == 200:
                    self.stats["ai_subsystem_health"] = "healthy"
                else:
                    self.stats["ai_subsystem_health"] = "degraded"
        except Exception:
            self.stats["ai_subsystem_health"] = "unhealthy"
        
        self.stats["last_health_check"] = datetime.now().isoformat()
        self.save_stats()
    
    def save_stats(self):
        try:
            with open(GATEWAY_STATS_FILE, "w") as f:
                json.dump(self.stats, f, indent=2)
        except Exception as e:
            logger.error(f"Failed to save stats: {e}")
    
    def load_stats(self):
        try:
            if os.path.exists(GATEWAY_STATS_FILE):
                with open(GATEWAY_STATS_FILE, "r") as f:
                    saved_stats = json.load(f)
                    self.stats.update(saved_stats)
        except Exception as e:
            logger.error(f"Failed to load stats: {e}")

# Initialize advanced components
security_manager = SecurityManager()
rate_limiter = RateLimiter()
ai_monitor = AIMonitor()
security = HTTPBearer(auto_error=False)

# Background health monitoring
async def background_health_monitor():
    while True:
        await ai_monitor.health_check()
        await asyncio.sleep(config.HEALTH_CHECK_INTERVAL)

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("🚀 Advanced AI Gateway starting up...")
    task = asyncio.create_task(background_health_monitor())
    yield
    # Shutdown
    task.cancel()
    logger.info("🛑 Advanced AI Gateway shutting down...")

# Create advanced FastAPI app
app = FastAPI(
    title="Aegis-2100 Advanced AI Gateway", 
    version="2.0.0",
    description="Advanced AI Security Bridge with authentication, monitoring, and intelligent routing",
    lifespan=lifespan
)

# Security Middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8085", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.add_middleware(
    TrustedHostMiddleware,
    allowed_hosts=["localhost", "127.0.0.1", "*.devvault.local"]
)

# Authentication dependency
async def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(security)):
    if not credentials:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    api_key = credentials.credentials
    user_data = security_manager.validate_api_key(api_key)
    
    if not user_data:
        raise HTTPException(status_code=401, detail="Invalid or expired API key")
    
    return user_data

# Rate limiting dependency
async def check_rate_limit(request: Request):
    client_host = getattr(request.client, 'host', 'unknown') if request.client else 'unknown'
    client_id = f"{client_host}:{request.headers.get('user-agent', 'unknown')}"
    if not rate_limiter.is_allowed(client_id):
        raise HTTPException(status_code=429, detail="Rate limit exceeded")

@app.get("/ai/status")
async def enhanced_status():
    return {
        "name": "Aegis-2100 Advanced AI Gateway",
        "version": "2.0.0",
        "disabled": is_disabled(),
        "ai_subsystem_health": ai_monitor.stats["ai_subsystem_health"],
        "last_health_check": ai_monitor.stats["last_health_check"],
        "uptime": datetime.now().isoformat(),
        "rate_limit": {
            "window_seconds": config.RATE_LIMIT_WINDOW,
            "max_requests": config.RATE_LIMIT_MAX_REQUESTS
        },
        "features": [
            "Authentication & API Keys",
            "Rate Limiting",
            "Health Monitoring", 
            "Request Analytics",
            "Intelligent Routing",
            "Security Controls"
        ]
    }

@app.post("/ai/auth/login")
async def login(auth_request: AuthRequest):
    # Simple auth for demo (in production, use proper user management)
    if auth_request.username == "admin" and auth_request.password == "devvault2025":
        token_data = {
            "sub": auth_request.username,
            "exp": datetime.utcnow() + timedelta(hours=24),
            "permissions": ["ai:admin", "ai:read", "ai:write"]
        }
        token = jwt.encode(token_data, config.JWT_SECRET_KEY, algorithm=config.JWT_ALGORITHM)
        return {"access_token": token, "token_type": "bearer"}
    else:
        raise HTTPException(status_code=401, detail="Invalid credentials")

@app.post("/ai/auth/api-key")
async def generate_api_key(
    key_request: APIKeyRequest,
    user_data: dict = Depends(get_current_user)
):
    if "ai:admin" not in user_data.get("permissions", []):
        raise HTTPException(status_code=403, detail="Admin permission required")
    
    api_key = security_manager.generate_api_key(
        key_request.name,
        key_request.permissions,
        key_request.expires_in_hours
    )
    
    return {
        "api_key": api_key,
        "name": key_request.name,
        "permissions": key_request.permissions,
        "expires_in_hours": key_request.expires_in_hours
    }

@app.post("/ai/disable")
async def advanced_disable(
    req: DisableRequest, 
    user_data: dict = Depends(get_current_user)
):
    if "ai:admin" not in user_data.get("permissions", []):
        raise HTTPException(status_code=403, detail="Admin permission required")
    
    try:
        if req.disable:
            with open(AI_DISABLED_FILE, "w") as f:
                json.dump({
                    "disabled_at": datetime.now().isoformat(),
                    "disabled_by": user_data.get("name", "unknown"),
                    "reason": req.reason
                }, f)
            logger.warning(f"AI disabled by {user_data.get('name')} - Reason: {req.reason}")
            return {"ok": True, "disabled": True, "reason": req.reason}
        else:
            if os.path.exists(AI_DISABLED_FILE):
                os.remove(AI_DISABLED_FILE)
            logger.info(f"AI enabled by {user_data.get('name')}")
            return {"ok": True, "disabled": False}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/ai/process")
async def intelligent_ai_processing(
    ai_request: AIRequest,
    background_tasks: BackgroundTasks,
    user_data: dict = Depends(get_current_user),
    _: None = Depends(check_rate_limit)
):
    if is_disabled():
        raise HTTPException(status_code=503, detail="AI subsystem is disabled")
    
    if "ai:write" not in user_data.get("permissions", []):
        raise HTTPException(status_code=403, detail="AI write permission required")
    
    start_time = time.time()
    
    try:
        # Route request to appropriate AI subsystem endpoint
        endpoint_map = {
            "validate": "/validate",
            "predict": "/predict", 
            "vision": "/vision/detect",
            "learn": "/learn/feedback"
        }
        
        endpoint = endpoint_map.get(ai_request.operation, "/process")
        url = f"{config.AI_SUBSYSTEM_URL}{endpoint}"
        
        # Add priority routing (high priority requests get faster processing)
        timeout = config.AI_TIMEOUT if ai_request.priority <= 5 else config.AI_TIMEOUT * 2
        
        async with httpx.AsyncClient(timeout=timeout) as client:
            response = await client.post(url, json={
                "data": ai_request.data,
                "metadata": {
                    "user": user_data.get("name"),
                    "priority": ai_request.priority,
                    "timestamp": datetime.now().isoformat(),
                    **(ai_request.metadata or {})
                }
            })
            
            response_time = time.time() - start_time
            
            if response.status_code == 200:
                result = response.json()
                background_tasks.add_task(ai_monitor.record_request, True, response_time)
                return {
                    "success": True,
                    "result": result,
                    "response_time_ms": round(response_time * 1000, 2),
                    "processed_by": "ai-subsystem"
                }
            else:
                background_tasks.add_task(ai_monitor.record_request, False, response_time)
                raise HTTPException(status_code=response.status_code, detail=response.text)
                
    except httpx.TimeoutException:
        background_tasks.add_task(ai_monitor.record_request, False, time.time() - start_time)
        raise HTTPException(status_code=504, detail="AI subsystem timeout")
    except Exception as e:
        background_tasks.add_task(ai_monitor.record_request, False, time.time() - start_time)
        logger.error(f"AI processing failed: {e}")
        raise HTTPException(status_code=500, detail=f"AI processing error: {str(e)}")

@app.get("/ai/analytics")
async def get_analytics(user_data: dict = Depends(get_current_user)):
    if "ai:read" not in user_data.get("permissions", []):
        raise HTTPException(status_code=403, detail="AI read permission required")
    
    return {
        "gateway_stats": ai_monitor.stats,
        "api_key_usage": {
            "total_keys": len(security_manager.api_keys),
            "active_keys": len([k for k in security_manager.api_keys.values() 
                              if datetime.fromisoformat(k["expires"]) > datetime.now()])
        },
        "performance_metrics": {
            "success_rate": (ai_monitor.stats["successful_requests"] / 
                           max(ai_monitor.stats["total_requests"], 1)) * 100,
            "average_response_time_ms": round(ai_monitor.stats["average_response_time"] * 1000, 2)
        }
    }

@app.get("/ai/health")
async def advanced_health_check():
    health_status = {
        "gateway": "healthy",
        "ai_subsystem": ai_monitor.stats["ai_subsystem_health"],
        "timestamp": datetime.now().isoformat(),
        "uptime_seconds": (datetime.now() - datetime.fromisoformat(ai_monitor.stats["uptime_start"])).total_seconds()
    }
    
    # Determine overall health
    if ai_monitor.stats["ai_subsystem_health"] == "unhealthy":
        health_status["status"] = "degraded"
    elif is_disabled():
        health_status["status"] = "disabled" 
    else:
        health_status["status"] = "healthy"
    
    return health_status

# IMPORTANT: This advanced gateway provides secure, monitored access to AI subsystem
# with authentication, rate limiting, and comprehensive analytics.
