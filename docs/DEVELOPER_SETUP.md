# 🔧 DevVault Pro X - Developer Setup Guide

**Complete setup instructions for contributors and developers**

---

## 🎯 **Prerequisites**

### **System Requirements:**
- **OS**: Windows 10+, Linux Ubuntu 18.04+, macOS 10.14+
- **Java**: JDK 21 or higher
- **Python**: 3.10+ with pip
- **Maven**: 3.8+ (for Java builds)
- **Git**: Latest version for version control
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code (recommended)

### **Recommended Hardware:**
- **RAM**: 16 GB or higher
- **Storage**: 50 GB SSD free space
- **Processor**: Intel i7 or AMD Ryzen 7
- **Graphics**: Dedicated GPU for advanced analytics (optional)

---

## 🛠️ **Step-by-Step Setup**

### **1. Clone the Repository:**
```bash
git clone https://github.com/RoboticsAITechLab/DevValuet.git
cd DevValuet
```

### **2. Java Development Setup:**
```bash
# Verify Java 21
java --version

# Install Maven dependencies
mvn clean install

# Compile the project
mvn compile
```

### **3. Python AI Subsystem Setup:**
```bash
# Create Python virtual environment
python -m venv .venv

# Activate virtual environment (Windows)
.venv\Scripts\activate

# Activate virtual environment (Linux/macOS)
source .venv/bin/activate

# Install Python dependencies
pip install -r requirements.txt
```

### **4. Database Setup:**
```bash
# PostgreSQL setup (optional - SQLite is default)
# Install PostgreSQL and create database
createdb devvault_prox

# Update application.properties with your database config
```

### **5. Run the Application:**
```bash
# Start AI subsystem (Terminal 1)
cd ai-subsystem
python app.py

# Start main application (Terminal 2)
cd backend-core
mvn spring-boot:run

# Launch JavaFX UI (Terminal 3)
cd desktop-ui
mvn javafx:run
```

---

## 🧩 **Development Environment Configuration**

### **IDE Setup (IntelliJ IDEA):**
1. **Import Project**: File → Open → Select DevValuet folder
2. **Set JDK**: File → Project Structure → Project Settings → Project → SDK → Choose JDK 21
3. **Maven Integration**: Enable auto-import for Maven projects
4. **Run Configurations**: Create run configs for Spring Boot and JavaFX modules
5. **Code Style**: Import code style from `docs/code-style.xml`

### **VS Code Setup:**
1. **Extensions**: Install Java Extension Pack, Python, Spring Boot
2. **Settings**: Configure Java home and Python interpreter
3. **Tasks**: Use provided `.vscode/tasks.json` for build automation
4. **Debug**: Configure launch.json for debugging Java and Python

---

## 🔧 **Custom Feature Development**

### **Plugin Development:**
1. **Create Plugin Class**: Implement `PluginInterface`
2. **Add Annotations**: Use `@DevVaultPlugin` and `@PluginFeature`
3. **Build Plugin JAR**: Package as separate JAR file
4. **Install Plugin**: Place in `plugins/` directory
5. **Test Integration**: Verify plugin loads and functions correctly

### **Adding New Features:**
```java
// Example: Custom Analytics Feature
@Component
@DevVaultFeature(name = "CustomAnalytics", version = "1.0")
public class CustomAnalyticsService {
    
    @Autowired
    private DataService dataService;
    
    @FeatureEndpoint("/api/custom/analytics")
    public AnalyticsResult processCustomData(Dataset data) {
        // Your custom feature implementation
        return performAnalysis(data);
    }
}
```

### **Database Extensions:**
1. **Create Entity**: Define JPA entity for new data model
2. **Repository**: Create Spring Data repository interface  
3. **Service Layer**: Implement business logic
4. **REST Controller**: Expose API endpoints
5. **Frontend Integration**: Add UI components in JavaFX

---

## 🧪 **Testing Guidelines**

### **Unit Tests:**
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=DataServiceTest

# Generate test coverage report
mvn jacoco:report
```

### **Integration Tests:**
```bash
# Run integration tests
mvn verify -P integration-tests

# Test with different profiles
mvn test -Dspring.profiles.active=test
```

### **UI Testing:**
```bash
# JavaFX UI tests with TestFX
mvn test -Dtest=UI*Test

# Python AI subsystem tests
cd ai-subsystem
python -m pytest tests/
```

---

## 📝 **Code Quality Standards**

### **Java Code Style:**
- **Formatting**: Use Google Java Style Guide
- **Naming**: CamelCase for classes, camelCase for methods/variables
- **Documentation**: Javadoc for public methods and classes
- **Testing**: Minimum 80% code coverage required

### **Python Code Style:**
- **Formatting**: Follow PEP 8 standards
- **Type Hints**: Use type annotations for all functions
- **Documentation**: Docstrings for all functions and classes
- **Testing**: pytest for unit tests with good coverage

### **Git Workflow:**
1. **Branch**: Create feature branch from main
2. **Commit**: Use conventional commit messages
3. **Test**: Ensure all tests pass locally
4. **PR**: Submit pull request with detailed description
5. **Review**: Address code review feedback
6. **Merge**: Squash and merge after approval

---

## 🚀 **Build and Deployment**

### **Development Build:**
```bash
# Full development build
mvn clean install -P development

# Quick build (skip tests)
mvn clean install -DskipTests
```

### **Production Build:**
```bash
# Production build with optimization
mvn clean install -P production

# Create distributable package
mvn package -P distribution
```

### **Docker Development:**
```bash
# Build development container
docker build -t devvault-prox-dev .

# Run with live reload
docker-compose up -d
```

---

## 📞 **Getting Help**

### **Community Support:**
- **GitHub Issues**: Report bugs and request features
- **Discussions**: Community Q&A and general help
- **Discord**: Real-time chat with other developers
- **Documentation**: Check DOCUMENTATION.md for detailed info

### **Contributor Support:**
- **Mentorship**: Experienced contributors help newcomers
- **Code Reviews**: Detailed feedback on contributions
- **Office Hours**: Weekly developer Q&A sessions
- **Revenue Sharing**: Get paid for valuable contributions!

---

**Happy Coding! 🎉**

*Ready to contribute to the future of enterprise data management?*