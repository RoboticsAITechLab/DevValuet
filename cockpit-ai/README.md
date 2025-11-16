# Aegis-2100 — Advanced AI Security Gateway 🚀

**Version 2.0.0** - Enterprise-grade AI security bridge with advanced monitoring, authentication, and intelligent routing.

## 🎯 **Key Features**

### 🔐 **Advanced Security**
- **JWT Authentication** with role-based permissions
- **API Key Management** with expiry and usage tracking
- **Rate Limiting** (100 requests/minute per client)
- **CORS & Trusted Host Protection**
- **Kill-switch functionality** for emergency shutdown

### 📊 **Intelligence & Monitoring**
- **Real-time Health Monitoring** of AI subsystem
- **Performance Analytics** with response time tracking
- **Request Analytics** with success/failure rates
- **Background Health Checks** every 30 seconds
- **Comprehensive Logging** for audit trails

### ⚡ **Advanced Routing**
- **Priority-based Request Handling** (1-10 priority levels)
- **Intelligent Endpoint Mapping** for different AI operations
- **Timeout Management** based on request priority
- **Concurrent Request Limiting** (max 50 concurrent)

## 🚀 **Quick Start**

```powershell
cd cockpit-ai
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt

# Start the advanced gateway
uvicorn app:app --port 8001 --reload
```

## 🔑 **Authentication**

### 1. Login to get JWT token:
```bash
curl -X POST "http://localhost:8001/ai/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "devvault2025"}'
```

### 2. Generate API Key:
```bash
curl -X POST "http://localhost:8001/ai/auth/api-key" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "my-app", "permissions": ["ai:read", "ai:write"], "expires_in_hours": 24}'
```

### 3. Use API Key for requests:
```bash
curl -H "Authorization: Bearer YOUR_API_KEY" \
  "http://localhost:8001/ai/status"
```

## 📡 **API Endpoints**

### **Status & Health**
- `GET /ai/status` - Enhanced gateway status
- `GET /ai/health` - Comprehensive health check
- `GET /ai/analytics` - Performance metrics & statistics

### **Authentication**
- `POST /ai/auth/login` - JWT authentication
- `POST /ai/auth/api-key` - Generate API keys

### **AI Operations**
- `POST /ai/process` - Intelligent AI request routing
- `POST /ai/disable` - Emergency kill-switch (admin only)

## 🧠 **AI Operations**

### **Supported Operations:**
```json
{
  "operation": "validate|predict|vision|learn",
  "data": {"your": "data"},
  "priority": 1-10,
  "metadata": {"additional": "info"}
}
```

### **Example Requests:**

#### **Neural Validation:**
```bash
curl -X POST "http://localhost:8001/ai/process" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "validate",
    "data": {"input": "data_to_validate"},
    "priority": 8
  }'
```

#### **Computer Vision:**
```bash
curl -X POST "http://localhost:8001/ai/process" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "vision",
    "data": {"image_data": "base64_encoded_image"},
    "priority": 9
  }'
```

## 📊 **Monitoring & Analytics**

### **Real-time Stats:**
```bash
curl -H "Authorization: Bearer YOUR_API_KEY" \
  "http://localhost:8001/ai/analytics"
```

**Returns:**
- Total requests processed
- Success/failure rates
- Average response times
- AI subsystem health status
- API key usage statistics
- Performance metrics

## 🔧 **Configuration**

### **Environment Variables:**
```bash
JWT_SECRET_KEY=your-secret-key
AI_ENABLED=true
RATE_LIMIT_MAX_REQUESTS=100
AI_TIMEOUT=60
```

### **Advanced Settings:**
- **Rate Limiting:** 100 requests/minute per client
- **Concurrent Requests:** Max 50 simultaneous
- **Health Checks:** Every 30 seconds
- **JWT Expiry:** 24 hours default
- **API Key Management:** Custom expiry times

## 🛡️ **Security Features**

### **Multi-layer Protection:**
1. **JWT Authentication** with role validation
2. **API Key Authorization** with usage tracking
3. **Rate Limiting** to prevent abuse
4. **CORS Protection** for web security
5. **Trusted Host Validation** 
6. **Request Logging** for audit trails

### **Permissions System:**
- `ai:read` - View AI status and analytics
- `ai:write` - Execute AI operations
- `ai:admin` - Full administrative access

## 🚨 **Emergency Controls**

### **Kill-switch:**
```bash
curl -X POST "http://localhost:8001/ai/disable" \
  -H "Authorization: Bearer ADMIN_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"disable": true, "reason": "Emergency shutdown"}'
```

## 📈 **Performance**

- **Async Processing** for high concurrency
- **Background Health Monitoring** 
- **Intelligent Request Routing**
- **Performance Analytics** with real-time metrics
- **Memory-efficient Rate Limiting**

## 🔄 **Integration**

### **With DevValuet Cockpit:**
```properties
# application.properties
devvault.ai.endpoint=http://localhost:8001
devvault.ai.timeout-seconds=60
devvault.ai.api-key=${AI_GATEWAY_API_KEY}
```

---

**Built for Enterprise DevValuet Platform** 🏢
**Secure • Intelligent • Scalable** ⚡
