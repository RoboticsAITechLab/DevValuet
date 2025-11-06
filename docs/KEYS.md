KEYS: secure keystore and rotation (cross-platform)

This document describes the simple cross-platform keystore helper used by the application and recommended operational steps.

Loading hierarchy
1. Per-alias environment variable: DEVVALUET_KEY_<ALIAS>
   - Example: DEVVALUET_KEY_token-encryption contains the Base64 key used for token encryption.
2. Encrypted keystore file: ~/.devvaluet/keystore.enc
   - The file is an AES-GCM encrypted JSON map { "alias" : "base64key" }
   - Decryption passphrase must be provided via DEVVALUET_KEYSTORE_PASSPHRASE environment variable.

Notes and operational guidance
- Prefer to place the Base64 key in environment variables when running in containers or CI secrets.
- If using the local keystore file, pick a strong passphrase and store it in your secret manager (Azure Key Vault, AWS Secrets Manager, GitHub Secrets).
- Rotation:
  - To rotate, generate a new random 32-byte key and Base64 encode it.
  - Use the provided SecureKeyStore.storeKey(alias, base64Key, passphrase) helper (or script) to add/replace the alias in the keystore file.
  - Deploy new key to environment or keystore file and restart the service.

Key initialization (non-interactive)

You can populate the encrypted keystore file non-interactively using the embedded runner. Example:

Windows (PowerShell):
```
java -jar app.jar --init-keystore=true --alias=token-encryption --key=<BASE64_KEY> --passphrase=<PASSPHRASE>
```

Or using environment variables:

Windows (PowerShell):
$env:DEVVALUET_INIT_KEYSTORE = "true"; $env:DEVVALUET_KEY_ALIAS = "token-encryption"; $env:DEVVALUET_KEY_VALUE = "<BASE64_KEY>"; $env:DEVVALUET_KEYSTORE_PASSPHRASE = "<PASSPHRASE>"; java -jar app.jar

Notes:
- The keystore file format now stores a 16-byte salt + 12-byte IV + ciphertext. PBKDF2WithHmacSHA256 (100k iterations) is used to derive the AES-256 key from the passphrase.
- The `KeyInitRunner` is intentionally simple and non-interactive to enable CI/CD scripts to seed secrets into a fresh machine.

Security caveats
- The keystore helper in this repository is intentionally small and uses SHA-256(passphrase) as KDF. For stronger security, use OS-native keystores or an external secrets manager.
- Do not commit keystore files or passphrases to source control.

Example (exporting env var for CI):

Windows (PowerShell):
$env:DEVVALUET_KEY_token_encryption = "<BASE64_KEY>"

Linux / macOS:
export DEVVALUET_KEY_token_encryption="<BASE64_KEY>"
