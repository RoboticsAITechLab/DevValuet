package com.devvault.frontend.dto;

import com.devvault.frontend.dto.base.BaseDto;
import com.devvault.frontend.validation.SmartValidator;
import com.devvault.frontend.encryption.QuantumSafeEncryption;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Next-Generation User DTO with Advanced AI Features
 * Implements quantum-safe security, biometric authentication, and behavioral analytics
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends BaseDto {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDto.class);
    
    // Basic User Information
    private String username;
    private String email;
    private String fullName;
    private String profileImageUrl;
    private UserRole role;
    private UserStatus status;
    
    // Enhanced Security Features
    private BiometricProfile biometricProfile;
    private SecurityContext securityContext;
    private List<SecurityPolicy> securityPolicies;
    private QuantumSecurityLevel quantumSecurityLevel;
    
    // Advanced AI Features
    private UserBehaviorAnalytics behaviorAnalytics;
    private PersonalizedPreferences preferences;
    private AIInsights aiInsights;
    private PredictiveMetrics predictiveMetrics;
    
    // Audit and Compliance
    private AccessHistory accessHistory;
    private ComplianceProfile complianceProfile;
    
    public UserDto() {
        super();
        this.biometricProfile = new BiometricProfile();
        this.securityContext = new SecurityContext();
        this.behaviorAnalytics = new UserBehaviorAnalytics();
        this.preferences = new PersonalizedPreferences();
        this.aiInsights = new AIInsights();
        this.predictiveMetrics = new PredictiveMetrics();
        this.accessHistory = new AccessHistory();
        this.complianceProfile = new ComplianceProfile();
        this.quantumSecurityLevel = QuantumSecurityLevel.STANDARD;
    }
    
    // ============================================
    // BIOMETRIC AUTHENTICATION
    // ============================================
    
    /**
     * Advanced biometric profile with multiple authentication factors
     */
    public static class BiometricProfile {
        private String fingerprintHash;
        private String voicePrintSignature;
        private FacialRecognitionData facialData;
        private BehavioralBiometrics behavioralPatterns;
        private QuantumBiometricEncryption quantumBiometric;
        
        public BiometricProfile() {
            this.facialData = new FacialRecognitionData();
            this.behavioralPatterns = new BehavioralBiometrics();
            this.quantumBiometric = new QuantumBiometricEncryption();
        }
        
        // Getters and setters
        public String getFingerprintHash() { return fingerprintHash; }
        public void setFingerprintHash(String fingerprintHash) { this.fingerprintHash = fingerprintHash; }
        
        public String getVoicePrintSignature() { return voicePrintSignature; }
        public void setVoicePrintSignature(String voicePrintSignature) { this.voicePrintSignature = voicePrintSignature; }
        
        public FacialRecognitionData getFacialData() { return facialData; }
        public void setFacialData(FacialRecognitionData facialData) { this.facialData = facialData; }
        
        public BehavioralBiometrics getBehavioralPatterns() { return behavioralPatterns; }
        public void setBehavioralPatterns(BehavioralBiometrics behavioralPatterns) { this.behavioralPatterns = behavioralPatterns; }
        
        public QuantumBiometricEncryption getQuantumBiometric() { return quantumBiometric; }
        public void setQuantumBiometric(QuantumBiometricEncryption quantumBiometric) { this.quantumBiometric = quantumBiometric; }
    }
    
    /**
     * Facial recognition data with 3D mapping
     */
    public static class FacialRecognitionData {
        private List<Double> faceEmbedding;
        private Map<String, Double> landmarkCoordinates;
        private String threedModel;
        private Double confidenceScore;
        private LocalDateTime lastScan;
        
        // Getters and setters
        public List<Double> getFaceEmbedding() { return faceEmbedding; }
        public void setFaceEmbedding(List<Double> faceEmbedding) { this.faceEmbedding = faceEmbedding; }
        
        public Map<String, Double> getLandmarkCoordinates() { return landmarkCoordinates; }
        public void setLandmarkCoordinates(Map<String, Double> landmarkCoordinates) { this.landmarkCoordinates = landmarkCoordinates; }
        
        public String getThreedModel() { return threedModel; }
        public void setThreedModel(String threedModel) { this.threedModel = threedModel; }
        
        public Double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
        
        public LocalDateTime getLastScan() { return lastScan; }
        public void setLastScan(LocalDateTime lastScan) { this.lastScan = lastScan; }
    }
    
    /**
     * Behavioral biometrics for continuous authentication
     */
    public static class BehavioralBiometrics {
        private TypingPattern typingPattern;
        private MouseMovementPattern mousePattern;
        private NavigationBehavior navigationBehavior;
        private ApplicationUsagePattern usagePattern;
        
        public BehavioralBiometrics() {
            this.typingPattern = new TypingPattern();
            this.mousePattern = new MouseMovementPattern();
            this.navigationBehavior = new NavigationBehavior();
            this.usagePattern = new ApplicationUsagePattern();
        }
        
        // Getters and setters
        public TypingPattern getTypingPattern() { return typingPattern; }
        public void setTypingPattern(TypingPattern typingPattern) { this.typingPattern = typingPattern; }
        
        public MouseMovementPattern getMousePattern() { return mousePattern; }
        public void setMousePattern(MouseMovementPattern mousePattern) { this.mousePattern = mousePattern; }
        
        public NavigationBehavior getNavigationBehavior() { return navigationBehavior; }
        public void setNavigationBehavior(NavigationBehavior navigationBehavior) { this.navigationBehavior = navigationBehavior; }
        
        public ApplicationUsagePattern getUsagePattern() { return usagePattern; }
        public void setUsagePattern(ApplicationUsagePattern usagePattern) { this.usagePattern = usagePattern; }
    }
    
    // ============================================
    // ADVANCED SECURITY CONTEXT
    // ============================================
    
    /**
     * Comprehensive security context with threat intelligence
     */
    public static class SecurityContext {
        private ThreatLevel currentThreatLevel;
        private List<SecurityIncident> recentIncidents;
        private SecurityScore securityScore;
        private QuantumThreatAssessment quantumThreatAssessment;
        private AIThreatDetection aiThreatDetection;
        
        public SecurityContext() {
            this.currentThreatLevel = ThreatLevel.LOW;
            this.securityScore = new SecurityScore();
            this.quantumThreatAssessment = new QuantumThreatAssessment();
            this.aiThreatDetection = new AIThreatDetection();
        }
        
        // Getters and setters
        public ThreatLevel getCurrentThreatLevel() { return currentThreatLevel; }
        public void setCurrentThreatLevel(ThreatLevel currentThreatLevel) { this.currentThreatLevel = currentThreatLevel; }
        
        public List<SecurityIncident> getRecentIncidents() { return recentIncidents; }
        public void setRecentIncidents(List<SecurityIncident> recentIncidents) { this.recentIncidents = recentIncidents; }
        
        public SecurityScore getSecurityScore() { return securityScore; }
        public void setSecurityScore(SecurityScore securityScore) { this.securityScore = securityScore; }
        
        public QuantumThreatAssessment getQuantumThreatAssessment() { return quantumThreatAssessment; }
        public void setQuantumThreatAssessment(QuantumThreatAssessment quantumThreatAssessment) { this.quantumThreatAssessment = quantumThreatAssessment; }
        
        public AIThreatDetection getAiThreatDetection() { return aiThreatDetection; }
        public void setAiThreatDetection(AIThreatDetection aiThreatDetection) { this.aiThreatDetection = aiThreatDetection; }
    }
    
    // ============================================
    // USER BEHAVIOR ANALYTICS
    // ============================================
    
    /**
     * Advanced user behavior analytics with machine learning
     */
    public static class UserBehaviorAnalytics {
        private ActivityPattern activityPattern;
        private PerformanceMetrics performanceMetrics;
        private AnomalyDetection anomalyDetection;
        private PredictiveBehavior predictiveBehavior;
        private NeuralBehaviorProfile neuralProfile;
        
        public UserBehaviorAnalytics() {
            this.activityPattern = new ActivityPattern();
            this.performanceMetrics = new PerformanceMetrics();
            this.anomalyDetection = new AnomalyDetection();
            this.predictiveBehavior = new PredictiveBehavior();
            this.neuralProfile = new NeuralBehaviorProfile();
        }
        
        // Getters and setters
        public ActivityPattern getActivityPattern() { return activityPattern; }
        public void setActivityPattern(ActivityPattern activityPattern) { this.activityPattern = activityPattern; }
        
        public PerformanceMetrics getPerformanceMetrics() { return performanceMetrics; }
        public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) { this.performanceMetrics = performanceMetrics; }
        
        public AnomalyDetection getAnomalyDetection() { return anomalyDetection; }
        public void setAnomalyDetection(AnomalyDetection anomalyDetection) { this.anomalyDetection = anomalyDetection; }
        
        public PredictiveBehavior getPredictiveBehavior() { return predictiveBehavior; }
        public void setPredictiveBehavior(PredictiveBehavior predictiveBehavior) { this.predictiveBehavior = predictiveBehavior; }
        
        public NeuralBehaviorProfile getNeuralProfile() { return neuralProfile; }
        public void setNeuralProfile(NeuralBehaviorProfile neuralProfile) { this.neuralProfile = neuralProfile; }
    }
    
    // ============================================
    // PERSONALIZED PREFERENCES WITH AI
    // ============================================
    
    /**
     * AI-driven personalized preferences
     */
    public static class PersonalizedPreferences {
        private UIPreferences uiPreferences;
        private WorkflowPreferences workflowPreferences;
        private NotificationPreferences notificationPreferences;
        private AIRecommendations aiRecommendations;
        private AdaptiveLearning adaptiveLearning;
        
        public PersonalizedPreferences() {
            this.uiPreferences = new UIPreferences();
            this.workflowPreferences = new WorkflowPreferences();
            this.notificationPreferences = new NotificationPreferences();
            this.aiRecommendations = new AIRecommendations();
            this.adaptiveLearning = new AdaptiveLearning();
        }
        
        // Getters and setters
        public UIPreferences getUiPreferences() { return uiPreferences; }
        public void setUiPreferences(UIPreferences uiPreferences) { this.uiPreferences = uiPreferences; }
        
        public WorkflowPreferences getWorkflowPreferences() { return workflowPreferences; }
        public void setWorkflowPreferences(WorkflowPreferences workflowPreferences) { this.workflowPreferences = workflowPreferences; }
        
        public NotificationPreferences getNotificationPreferences() { return notificationPreferences; }
        public void setNotificationPreferences(NotificationPreferences notificationPreferences) { this.notificationPreferences = notificationPreferences; }
        
        public AIRecommendations getAiRecommendations() { return aiRecommendations; }
        public void setAiRecommendations(AIRecommendations aiRecommendations) { this.aiRecommendations = aiRecommendations; }
        
        public AdaptiveLearning getAdaptiveLearning() { return adaptiveLearning; }
        public void setAdaptiveLearning(AdaptiveLearning adaptiveLearning) { this.adaptiveLearning = adaptiveLearning; }
    }
    
    // ============================================
    // SUPPORT CLASSES
    // ============================================
    
    // User enums
    public enum UserRole {
        SUPER_ADMIN, ADMIN, DEVELOPER, ANALYST, VIEWER, AI_AGENT
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION, AI_ENHANCED
    }
    
    public enum ThreatLevel {
        LOW, MEDIUM, HIGH, CRITICAL, QUANTUM_THREAT
    }
    
    public enum QuantumSecurityLevel {
        STANDARD, ENHANCED, MAXIMUM, QUANTUM_SECURE
    }
    
    // Additional support classes (simplified for brevity)
    public static class QuantumBiometricEncryption {
        private String quantumSignature;
        private LocalDateTime lastUpdate;
        
        public String getQuantumSignature() { return quantumSignature; }
        public void setQuantumSignature(String quantumSignature) { this.quantumSignature = quantumSignature; }
        
        public LocalDateTime getLastUpdate() { return lastUpdate; }
        public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    }
    
    public static class TypingPattern {
        private Double averageSpeed;
        private Map<String, Double> keyPressIntervals;
        private Double rhythmSignature;
        
        public Double getAverageSpeed() { return averageSpeed; }
        public void setAverageSpeed(Double averageSpeed) { this.averageSpeed = averageSpeed; }
        
        public Map<String, Double> getKeyPressIntervals() { return keyPressIntervals; }
        public void setKeyPressIntervals(Map<String, Double> keyPressIntervals) { this.keyPressIntervals = keyPressIntervals; }
        
        public Double getRhythmSignature() { return rhythmSignature; }
        public void setRhythmSignature(Double rhythmSignature) { this.rhythmSignature = rhythmSignature; }
    }
    
    public static class MouseMovementPattern {
        private List<Double> velocityProfile;
        private Double clickPattern;
        private String movementSignature;
        
        public List<Double> getVelocityProfile() { return velocityProfile; }
        public void setVelocityProfile(List<Double> velocityProfile) { this.velocityProfile = velocityProfile; }
        
        public Double getClickPattern() { return clickPattern; }
        public void setClickPattern(Double clickPattern) { this.clickPattern = clickPattern; }
        
        public String getMovementSignature() { return movementSignature; }
        public void setMovementSignature(String movementSignature) { this.movementSignature = movementSignature; }
    }
    
    public static class NavigationBehavior {
        private List<String> frequentPaths;
        private Map<String, Integer> pageVisitCounts;
        private Double navigationEfficiency;
        
        public List<String> getFrequentPaths() { return frequentPaths; }
        public void setFrequentPaths(List<String> frequentPaths) { this.frequentPaths = frequentPaths; }
        
        public Map<String, Integer> getPageVisitCounts() { return pageVisitCounts; }
        public void setPageVisitCounts(Map<String, Integer> pageVisitCounts) { this.pageVisitCounts = pageVisitCounts; }
        
        public Double getNavigationEfficiency() { return navigationEfficiency; }
        public void setNavigationEfficiency(Double navigationEfficiency) { this.navigationEfficiency = navigationEfficiency; }
    }
    
    public static class ApplicationUsagePattern {
        private Map<String, Long> featureUsageTime;
        private List<String> preferredWorkflows;
        private Double productivityScore;
        
        public Map<String, Long> getFeatureUsageTime() { return featureUsageTime; }
        public void setFeatureUsageTime(Map<String, Long> featureUsageTime) { this.featureUsageTime = featureUsageTime; }
        
        public List<String> getPreferredWorkflows() { return preferredWorkflows; }
        public void setPreferredWorkflows(List<String> preferredWorkflows) { this.preferredWorkflows = preferredWorkflows; }
        
        public Double getProductivityScore() { return productivityScore; }
        public void setProductivityScore(Double productivityScore) { this.productivityScore = productivityScore; }
    }
    
    public static class SecurityIncident {
        private String incidentId;
        private LocalDateTime timestamp;
        private String description;
        private String severity;
        private String resolution;
        
        // Getters and setters
        public String getIncidentId() { return incidentId; }
        public void setIncidentId(String incidentId) { this.incidentId = incidentId; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
    }
    
    public static class SecurityScore {
        private Double overallScore;
        private Map<String, Double> componentScores;
        private LocalDateTime lastCalculated;
        
        public Double getOverallScore() { return overallScore; }
        public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
        
        public Map<String, Double> getComponentScores() { return componentScores; }
        public void setComponentScores(Map<String, Double> componentScores) { this.componentScores = componentScores; }
        
        public LocalDateTime getLastCalculated() { return lastCalculated; }
        public void setLastCalculated(LocalDateTime lastCalculated) { this.lastCalculated = lastCalculated; }
    }
    
    // Additional simplified classes
    public static class QuantumThreatAssessment {
        private String threatLevel;
        private LocalDateTime lastAssessment;
        
        public String getThreatLevel() { return threatLevel; }
        public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }
        
        public LocalDateTime getLastAssessment() { return lastAssessment; }
        public void setLastAssessment(LocalDateTime lastAssessment) { this.lastAssessment = lastAssessment; }
    }
    
    public static class AIThreatDetection {
        private Double threatProbability;
        private List<String> detectedAnomalies;
        
        public Double getThreatProbability() { return threatProbability; }
        public void setThreatProbability(Double threatProbability) { this.threatProbability = threatProbability; }
        
        public List<String> getDetectedAnomalies() { return detectedAnomalies; }
        public void setDetectedAnomalies(List<String> detectedAnomalies) { this.detectedAnomalies = detectedAnomalies; }
    }
    
    public static class ActivityPattern {
        private Map<String, Integer> hourlyActivity;
        private List<String> peakHours;
        
        public Map<String, Integer> getHourlyActivity() { return hourlyActivity; }
        public void setHourlyActivity(Map<String, Integer> hourlyActivity) { this.hourlyActivity = hourlyActivity; }
        
        public List<String> getPeakHours() { return peakHours; }
        public void setPeakHours(List<String> peakHours) { this.peakHours = peakHours; }
    }
    
    public static class PerformanceMetrics {
        private Double efficiency;
        private Double accuracy;
        
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
        
        public Double getAccuracy() { return accuracy; }
        public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
    }
    
    public static class AnomalyDetection {
        private List<String> detectedAnomalies;
        private Double anomalyScore;
        
        public List<String> getDetectedAnomalies() { return detectedAnomalies; }
        public void setDetectedAnomalies(List<String> detectedAnomalies) { this.detectedAnomalies = detectedAnomalies; }
        
        public Double getAnomalyScore() { return anomalyScore; }
        public void setAnomalyScore(Double anomalyScore) { this.anomalyScore = anomalyScore; }
    }
    
    public static class PredictiveBehavior {
        private List<String> predictedActions;
        private Double confidenceLevel;
        
        public List<String> getPredictedActions() { return predictedActions; }
        public void setPredictedActions(List<String> predictedActions) { this.predictedActions = predictedActions; }
        
        public Double getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(Double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    }
    
    public static class NeuralBehaviorProfile {
        private List<Double> behaviorEmbedding;
        private String profileSignature;
        
        public List<Double> getBehaviorEmbedding() { return behaviorEmbedding; }
        public void setBehaviorEmbedding(List<Double> behaviorEmbedding) { this.behaviorEmbedding = behaviorEmbedding; }
        
        public String getProfileSignature() { return profileSignature; }
        public void setProfileSignature(String profileSignature) { this.profileSignature = profileSignature; }
    }
    
    public static class UIPreferences {
        private String theme;
        private String layout;
        
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        
        public String getLayout() { return layout; }
        public void setLayout(String layout) { this.layout = layout; }
    }
    
    public static class WorkflowPreferences {
        private List<String> preferredWorkflows;
        private Map<String, Boolean> enabledFeatures;
        
        public List<String> getPreferredWorkflows() { return preferredWorkflows; }
        public void setPreferredWorkflows(List<String> preferredWorkflows) { this.preferredWorkflows = preferredWorkflows; }
        
        public Map<String, Boolean> getEnabledFeatures() { return enabledFeatures; }
        public void setEnabledFeatures(Map<String, Boolean> enabledFeatures) { this.enabledFeatures = enabledFeatures; }
    }
    
    public static class NotificationPreferences {
        private Boolean emailEnabled;
        private Boolean pushEnabled;
        
        public Boolean getEmailEnabled() { return emailEnabled; }
        public void setEmailEnabled(Boolean emailEnabled) { this.emailEnabled = emailEnabled; }
        
        public Boolean getPushEnabled() { return pushEnabled; }
        public void setPushEnabled(Boolean pushEnabled) { this.pushEnabled = pushEnabled; }
    }
    
    public static class AIRecommendations {
        private List<String> recommendations;
        private Double relevanceScore;
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public Double getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(Double relevanceScore) { this.relevanceScore = relevanceScore; }
    }
    
    public static class AdaptiveLearning {
        private Map<String, Double> learningWeights;
        private String adaptationStrategy;
        
        public Map<String, Double> getLearningWeights() { return learningWeights; }
        public void setLearningWeights(Map<String, Double> learningWeights) { this.learningWeights = learningWeights; }
        
        public String getAdaptationStrategy() { return adaptationStrategy; }
        public void setAdaptationStrategy(String adaptationStrategy) { this.adaptationStrategy = adaptationStrategy; }
    }
    
    public static class SecurityPolicy {
        private String policyId;
        private String name;
        private String description;
        private Boolean enabled;
        
        // Getters and setters
        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }
    
    public static class AccessHistory {
        private List<String> recentAccess;
        private Map<String, Long> accessPatterns;
        
        public List<String> getRecentAccess() { return recentAccess; }
        public void setRecentAccess(List<String> recentAccess) { this.recentAccess = recentAccess; }
        
        public Map<String, Long> getAccessPatterns() { return accessPatterns; }
        public void setAccessPatterns(Map<String, Long> accessPatterns) { this.accessPatterns = accessPatterns; }
    }
    
    public static class ComplianceProfile {
        private String complianceLevel;
        private List<String> certifications;
        
        public String getComplianceLevel() { return complianceLevel; }
        public void setComplianceLevel(String complianceLevel) { this.complianceLevel = complianceLevel; }
        
        public List<String> getCertifications() { return certifications; }
        public void setCertifications(List<String> certifications) { this.certifications = certifications; }
    }
    
    // ============================================
    // MAIN GETTERS AND SETTERS
    // ============================================
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public BiometricProfile getBiometricProfile() { return biometricProfile; }
    public void setBiometricProfile(BiometricProfile biometricProfile) { this.biometricProfile = biometricProfile; }
    
    public SecurityContext getSecurityContext() { return securityContext; }
    public void setSecurityContext(SecurityContext securityContext) { this.securityContext = securityContext; }
    
    public List<SecurityPolicy> getSecurityPolicies() { return securityPolicies; }
    public void setSecurityPolicies(List<SecurityPolicy> securityPolicies) { this.securityPolicies = securityPolicies; }
    
    public QuantumSecurityLevel getQuantumSecurityLevel() { return quantumSecurityLevel; }
    public void setQuantumSecurityLevel(QuantumSecurityLevel quantumSecurityLevel) { this.quantumSecurityLevel = quantumSecurityLevel; }
    
    public UserBehaviorAnalytics getBehaviorAnalytics() { return behaviorAnalytics; }
    public void setBehaviorAnalytics(UserBehaviorAnalytics behaviorAnalytics) { this.behaviorAnalytics = behaviorAnalytics; }
    
    public PersonalizedPreferences getPreferences() { return preferences; }
    public void setPreferences(PersonalizedPreferences preferences) { this.preferences = preferences; }
    
    public AIInsights getAiInsights() { return aiInsights; }
    public void setAiInsights(AIInsights aiInsights) { this.aiInsights = aiInsights; }
    
    public PredictiveMetrics getPredictiveMetrics() { return predictiveMetrics; }
    public void setPredictiveMetrics(PredictiveMetrics predictiveMetrics) { this.predictiveMetrics = predictiveMetrics; }
    
    public AccessHistory getAccessHistory() { return accessHistory; }
    public void setAccessHistory(AccessHistory accessHistory) { this.accessHistory = accessHistory; }
    
    public ComplianceProfile getComplianceProfile() { return complianceProfile; }
    public void setComplianceProfile(ComplianceProfile complianceProfile) { this.complianceProfile = complianceProfile; }
    
    // ============================================
    // VALIDATION METHODS
    // ============================================
    
    @Override
    public boolean validateData() {
        if (!super.validateData()) {
            return false;
        }
        
        try {
            SmartValidator validator = SmartValidator.getInstance();
            
            // Validate username
            var usernameResult = validator.validateField("username", username, "required|min:3|max:50|alphanumeric");
            if (!usernameResult.isValid) {
                return false;
            }
            
            // Validate email
            var emailResult = validator.validateField("email", email, "required|email");
            if (!emailResult.isValid) {
                return false;
            }
            
            // Validate full name
            var nameResult = validator.validateField("fullName", fullName, "required|min:2|max:100");
            if (!nameResult.isValid) {
                return false;
            }
            
            // Validate security level
            if (quantumSecurityLevel == null) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("User validation failed", e);
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("UserDto{username='%s', email='%s', role=%s, status=%s, quantumSecurityLevel=%s}", 
                username, email, role, status, quantumSecurityLevel);
    }
}