# Advanced AI Gateway Startup Script
# Run this to start the enhanced Aegis-2100 AI Gateway

Write-Host "🚀 Starting Advanced AI Gateway (Aegis-2100 v2.0)..." -ForegroundColor Green
Write-Host ""

# Check if virtual environment exists
if (!(Test-Path ".\.venv")) {
    Write-Host "📦 Creating virtual environment..." -ForegroundColor Yellow
    python -m venv .venv
}

# Activate virtual environment
Write-Host "🔄 Activating virtual environment..." -ForegroundColor Cyan
& .\.venv\Scripts\Activate.ps1

# Upgrade pip and install dependencies
Write-Host "📥 Installing/updating dependencies..." -ForegroundColor Yellow
python -m pip install --upgrade pip
pip install --upgrade typing-extensions
pip install -r requirements.txt

Write-Host ""
Write-Host "✅ Dependencies installed successfully!" -ForegroundColor Green

# Display gateway information
Write-Host ""
Write-Host "🛡️  AEGIS-2100 ADVANCED AI GATEWAY" -ForegroundColor Magenta
Write-Host "=====================================" -ForegroundColor Magenta
Write-Host "🔹 Version: 2.0.0" -ForegroundColor White
Write-Host "🔹 Port: 8001" -ForegroundColor White
Write-Host "🔹 Features:" -ForegroundColor White
Write-Host "   • JWT Authentication & API Keys" -ForegroundColor Gray
Write-Host "   • Rate Limiting (100 req/min)" -ForegroundColor Gray
Write-Host "   • Health Monitoring" -ForegroundColor Gray
Write-Host "   • Request Analytics" -ForegroundColor Gray
Write-Host "   • Intelligent Routing" -ForegroundColor Gray
Write-Host "   • Priority Queue System" -ForegroundColor Gray
Write-Host "   • Emergency Kill-switch" -ForegroundColor Gray
Write-Host ""

# Start the gateway
Write-Host "🚀 Starting Advanced AI Gateway on port 8001..." -ForegroundColor Green
Write-Host ""

# Test import first
try {
    python -c "from app import app; print('✅ Advanced Gateway loaded successfully!')"
    Write-Host ""
    Write-Host "🌐 Access the gateway at: http://localhost:8001" -ForegroundColor Cyan
    Write-Host "📊 Health check: http://localhost:8001/ai/health" -ForegroundColor Cyan  
    Write-Host "📈 Analytics: http://localhost:8001/ai/analytics" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "🔑 Default admin credentials:" -ForegroundColor Yellow
    Write-Host "   Username: admin" -ForegroundColor White
    Write-Host "   Password: devvault2025" -ForegroundColor White
    Write-Host ""
    
    # Start the server
    uvicorn app:app --host 0.0.0.0 --port 8001 --reload
}
catch {
    Write-Host "❌ Error loading advanced gateway: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Troubleshooting:" -ForegroundColor Yellow
    Write-Host "   1. Check if all dependencies are installed" -ForegroundColor White
    Write-Host "   2. Verify Python environment" -ForegroundColor White
    Write-Host "   3. Check requirements.txt" -ForegroundColor White
}