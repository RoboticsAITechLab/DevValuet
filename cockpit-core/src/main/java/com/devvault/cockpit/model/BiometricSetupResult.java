package com.devvault.cockpit.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Advanced biometric authentication setup and configuration result
 */
public class BiometricSetupResult {
    private String setupId;
    private LocalDateTime setupTime;
    private boolean successful;
    private List<String> enabledMethods;
    private FaceRecognitionSetup faceRecognition;
    private FingerprintSetup fingerprint;
    private VoiceRecognitionSetup voiceRecognition;
    private IrisSetup iris;
    private BehavioralBiometrics behavioral;
    private String errorMessage;
    private Map<String, Object> configurationData;
    
    // Constructors
    public BiometricSetupResult() {}
    
    // Getters and Setters
    public String getSetupId() { return setupId; }
    public void setSetupId(String setupId) { this.setupId = setupId; }
    
    public LocalDateTime getSetupTime() { return setupTime; }
    public void setSetupTime(LocalDateTime setupTime) { this.setupTime = setupTime; }
    
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public List<String> getEnabledMethods() { return enabledMethods; }
    public void setEnabledMethods(List<String> enabledMethods) { this.enabledMethods = enabledMethods; }
    
    public FaceRecognitionSetup getFaceRecognition() { return faceRecognition; }
    public void setFaceRecognition(FaceRecognitionSetup faceRecognition) { this.faceRecognition = faceRecognition; }
    
    public FingerprintSetup getFingerprint() { return fingerprint; }
    public void setFingerprint(FingerprintSetup fingerprint) { this.fingerprint = fingerprint; }
    
    public VoiceRecognitionSetup getVoiceRecognition() { return voiceRecognition; }
    public void setVoiceRecognition(VoiceRecognitionSetup voiceRecognition) { this.voiceRecognition = voiceRecognition; }
    
    public IrisSetup getIris() { return iris; }
    public void setIris(IrisSetup iris) { this.iris = iris; }
    
    public BehavioralBiometrics getBehavioral() { return behavioral; }
    public void setBehavioral(BehavioralBiometrics behavioral) { this.behavioral = behavioral; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Map<String, Object> getConfigurationData() { return configurationData; }
    public void setConfigurationData(Map<String, Object> configurationData) { this.configurationData = configurationData; }
    
    // Additional methods needed by service
    public boolean isSuccess() { return successful; }
    public void setSuccess(boolean success) { this.successful = success; }
    
    public void setFingerprintSetup(FingerprintSetup fingerprintSetup) { this.fingerprint = fingerprintSetup; }
    public void setIrisSetup(IrisSetup irisSetup) { this.iris = irisSetup; }
    public void setBehavioralBiometrics(BehavioralBiometrics behavioralBiometrics) { this.behavioral = behavioralBiometrics; }
}