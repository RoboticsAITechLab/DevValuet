package com.devvault.frontend.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Animation Utilities
 * Provides glassmorphism effects, transitions, and animations for the neo-glass interface
 */
public class AnimationUtils {
    
    // Transition durations
    public static final Duration FAST_DURATION = Duration.millis(200);
    public static final Duration NORMAL_DURATION = Duration.millis(300);
    public static final Duration SLOW_DURATION = Duration.millis(500);
    
    /**
     * Add soft pulse glow effect on hover
     */
    public static void addPulseHover(Node node) {
        DropShadow glowEffect = new DropShadow();
        glowEffect.setColor(Color.CYAN);
        glowEffect.setRadius(0);
        glowEffect.setSpread(0.3);
        
        // Hover animation
        Timeline pulseIn = new Timeline(
            new KeyFrame(NORMAL_DURATION,
                new KeyValue(glowEffect.radiusProperty(), 15),
                new KeyValue(node.scaleXProperty(), 1.05),
                new KeyValue(node.scaleYProperty(), 1.05)
            )
        );
        
        Timeline pulseOut = new Timeline(
            new KeyFrame(NORMAL_DURATION,
                new KeyValue(glowEffect.radiusProperty(), 0),
                new KeyValue(node.scaleXProperty(), 1.0),
                new KeyValue(node.scaleYProperty(), 1.0)
            )
        );
        
        node.setOnMouseEntered(e -> {
            node.setEffect(glowEffect);
            pulseOut.stop();
            pulseIn.play();
        });
        
        node.setOnMouseExited(e -> {
            pulseIn.stop();
            pulseOut.setOnFinished(event -> node.setEffect(null));
            pulseOut.play();
        });
    }
    
    /**
     * Add magnetic ripple effect on click
     */
    public static void addRippleClick(Button button) {
        button.setOnMousePressed(e -> {
            // Scale down animation
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(0.95);
            scaleDown.setToY(0.95);
            
            // Scale back up animation
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            
            // Ripple effect with color change
            FillTransition colorChange = new FillTransition(Duration.millis(200), 
                Color.TRANSPARENT, Color.CYAN.deriveColor(0, 1, 1, 0.3));
            
            SequentialTransition ripple = new SequentialTransition(scaleDown, scaleUp);
            ripple.play();
        });
    }
    
    /**
     * Add slide-in animation for scene transitions
     */
    public static void slideIn(Node node, Direction direction) {
        double startX = 0, startY = 0;
        
        switch (direction) {
            case LEFT -> startX = -node.getBoundsInParent().getWidth();
            case RIGHT -> startX = node.getBoundsInParent().getWidth();
            case TOP -> startY = -node.getBoundsInParent().getHeight();
            case BOTTOM -> startY = node.getBoundsInParent().getHeight();
        }
        
        node.setTranslateX(startX);
        node.setTranslateY(startY);
        node.setOpacity(0);
        
        TranslateTransition slide = new TranslateTransition(SLOW_DURATION, node);
        slide.setToX(0);
        slide.setToY(0);
        
        FadeTransition fade = new FadeTransition(SLOW_DURATION, node);
        fade.setToValue(1.0);
        
        ParallelTransition slideIn = new ParallelTransition(slide, fade);
        slideIn.play();
    }
    
    /**
     * Add fade transition between scenes
     */
    public static void fadeTransition(Node oldNode, Node newNode) {
        FadeTransition fadeOut = new FadeTransition(NORMAL_DURATION, oldNode);
        fadeOut.setToValue(0);
        
        FadeTransition fadeIn = new FadeTransition(NORMAL_DURATION, newNode);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        fadeOut.setOnFinished(e -> fadeIn.play());
        fadeOut.play();
    }
    
    /**
     * Add glassmorphism blur effect
     */
    public static void addGlassmorphism(Node node) {
        GaussianBlur blur = new GaussianBlur(10);
        node.setEffect(blur);
        node.setStyle(node.getStyle() + "; -fx-background-color: rgba(255, 255, 255, 0.1);");
    }
    
    /**
     * Add loading spinner animation
     */
    public static RotateTransition createLoadingSpinner(Node node) {
        RotateTransition spinner = new RotateTransition(Duration.seconds(1), node);
        spinner.setByAngle(360);
        spinner.setCycleCount(Timeline.INDEFINITE);
        spinner.setInterpolator(Interpolator.LINEAR);
        return spinner;
    }
    
    /**
     * Add floating animation for elements
     */
    public static void addFloatingAnimation(Node node) {
        TranslateTransition float1 = new TranslateTransition(Duration.seconds(2), node);
        float1.setByY(-5);
        float1.setAutoReverse(true);
        float1.setCycleCount(Timeline.INDEFINITE);
        float1.setInterpolator(Interpolator.EASE_BOTH);
        float1.play();
    }
    
    /**
     * Add typing animation for text
     */
    public static void addTypingAnimation(javafx.scene.text.Text textNode, String fullText) {
        Timeline timeline = new Timeline();
        
        for (int i = 0; i <= fullText.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(i * 50),
                e -> textNode.setText(fullText.substring(0, index))
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.play();
    }
    
    /**
     * Add progress bar animation
     */
    public static void animateProgress(javafx.scene.control.ProgressBar progressBar, double targetValue) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1),
                new KeyValue(progressBar.progressProperty(), targetValue))
        );
        timeline.play();
    }
    
    /**
     * Add glow hover effect to buttons
     */
    public static void addGlowHover(Button button) {
        DropShadow glowEffect = new DropShadow();
        glowEffect.setColor(Color.CYAN);
        glowEffect.setRadius(0);
        glowEffect.setSpread(0.3);
        
        button.setOnMouseEntered(e -> {
            button.setEffect(glowEffect);
            Timeline glow = new Timeline(
                new KeyFrame(NORMAL_DURATION,
                    new KeyValue(glowEffect.radiusProperty(), 10)
                )
            );
            glow.play();
        });
        
        button.setOnMouseExited(e -> {
            Timeline unglow = new Timeline(
                new KeyFrame(NORMAL_DURATION,
                    new KeyValue(glowEffect.radiusProperty(), 0)
                )
            );
            unglow.setOnFinished(ev -> button.setEffect(null));
            unglow.play();
        });
    }
    
    /**
     * Add ripple click effect
     */
    public static void addRippleClick(Node node) {
        node.setOnMousePressed(e -> {
            ScaleTransition shrink = new ScaleTransition(Duration.millis(100), node);
            shrink.setToX(0.95);
            shrink.setToY(0.95);
            shrink.play();
        });
        
        node.setOnMouseReleased(e -> {
            ScaleTransition grow = new ScaleTransition(Duration.millis(100), node);
            grow.setToX(1.0);
            grow.setToY(1.0);
            grow.play();
        });
    }
    
    /**
     * Create pulse animation for nodes
     */
    public static void createPulseAnimation(Node node, int duration) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(duration), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.02);
        pulse.setToY(1.02);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();
    }
    
    /**
     * Create progress animation for buttons with completion callback
     */
    public static void createProgressAnimation(Button button, int duration, Runnable onComplete) {
        String originalText = button.getText();
        button.setDisable(true);
        
        Timeline animation = new Timeline();
        for (int i = 0; i <= 100; i += 10) {
            final int progress = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(duration * progress / 100.0),
                e -> button.setText(originalText + " (" + progress + "%)")
            );
            animation.getKeyFrames().add(keyFrame);
        }
        
        animation.setOnFinished(e -> {
            button.setText(originalText);
            button.setDisable(false);
            if (onComplete != null) {
                onComplete.run();
            }
        });
        
        animation.play();
    }
    
    /**
     * Create progress animation for progress bars with completion callback
     */
    public static void createProgressAnimation(javafx.scene.control.ProgressBar progressBar, int duration, Runnable onComplete) {
        Timeline animation = new Timeline(
            new KeyFrame(Duration.millis(duration),
                new KeyValue(progressBar.progressProperty(), 1.0))
        );
        
        animation.setOnFinished(e -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
        
        animation.play();
    }
    
    /**
     * Create slide in animation for nodes
     */
    public static void createSlideInAnimation(Node node) {
        TranslateTransition slide = new TranslateTransition(NORMAL_DURATION, node);
        slide.setFromX(-100);
        slide.setToX(0);
        
        FadeTransition fade = new FadeTransition(NORMAL_DURATION, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ParallelTransition slideIn = new ParallelTransition(slide, fade);
        slideIn.play();
    }
    
    /**
     * Create slide out animation for nodes with completion callback
     */
    public static void createSlideOutAnimation(Node node, Runnable onComplete) {
        TranslateTransition slide = new TranslateTransition(NORMAL_DURATION, node);
        slide.setFromX(0);
        slide.setToX(100);
        
        FadeTransition fade = new FadeTransition(NORMAL_DURATION, node);
        fade.setFromValue(1);
        fade.setToValue(0);
        
        ParallelTransition slideOut = new ParallelTransition(slide, fade);
        slideOut.setOnFinished(e -> {
            if (onComplete != null) {
                onComplete.run();
            }
        });
        slideOut.play();
    }
    
    /**
     * Advanced particle-like floating animation
     */
    public static void addFloatingEffect(Node node) {
        TranslateTransition floatX = new TranslateTransition(Duration.seconds(3), node);
        floatX.setFromX(-2);
        floatX.setToX(2);
        floatX.setCycleCount(Timeline.INDEFINITE);
        floatX.setAutoReverse(true);
        
        TranslateTransition floatY = new TranslateTransition(Duration.seconds(4), node);
        floatY.setFromY(-1);
        floatY.setToY(1);
        floatY.setCycleCount(Timeline.INDEFINITE);
        floatY.setAutoReverse(true);
        
        ParallelTransition floating = new ParallelTransition(floatX, floatY);
        floating.play();
    }
    
    /**
     * Matrix-style data stream effect
     */
    public static void addDataStreamEffect(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(1200), node);
        scale.setFromX(0.98);
        scale.setFromY(0.98);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setCycleCount(Timeline.INDEFINITE);
        scale.setAutoReverse(true);
        
        ParallelTransition dataStream = new ParallelTransition(fade, scale);
        dataStream.play();
    }
    
    /**
     * Cyber-security scanning effect
     */
    public static void addScanningEffect(Node node) {
        DropShadow scanEffect = new DropShadow();
        scanEffect.setColor(Color.LIME);
        scanEffect.setRadius(0);
        
        Timeline scanAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(scanEffect.radiusProperty(), 0),
                new KeyValue(scanEffect.spreadProperty(), 0)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(scanEffect.radiusProperty(), 15),
                new KeyValue(scanEffect.spreadProperty(), 0.8)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(scanEffect.radiusProperty(), 0),
                new KeyValue(scanEffect.spreadProperty(), 0)
            )
        );
        
        scanAnimation.setCycleCount(Timeline.INDEFINITE);
        node.setEffect(scanEffect);
        scanAnimation.play();
    }
    
    /**
     * AI Processing indicator with neural network effect
     */
    public static void addNeuralProcessingEffect(Node node) {
        DropShadow neuralGlow = new DropShadow();
        neuralGlow.setColor(Color.MEDIUMORCHID);
        neuralGlow.setRadius(5);
        neuralGlow.setSpread(0.4);
        
        Timeline processingAnimation = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(neuralGlow.radiusProperty(), 5),
                new KeyValue(neuralGlow.spreadProperty(), 0.4)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(neuralGlow.radiusProperty(), 12),
                new KeyValue(neuralGlow.spreadProperty(), 0.7)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(neuralGlow.radiusProperty(), 8),
                new KeyValue(neuralGlow.spreadProperty(), 0.5)
            ),
            new KeyFrame(Duration.millis(900),
                new KeyValue(neuralGlow.radiusProperty(), 15),
                new KeyValue(neuralGlow.spreadProperty(), 0.8)
            ),
            new KeyFrame(Duration.millis(1200),
                new KeyValue(neuralGlow.radiusProperty(), 5),
                new KeyValue(neuralGlow.spreadProperty(), 0.4)
            )
        );
        
        processingAnimation.setCycleCount(Timeline.INDEFINITE);
        node.setEffect(neuralGlow);
        processingAnimation.play();
    }
    
    /**
     * Direction enum for slide animations
     */
    public enum Direction {
        LEFT, RIGHT, TOP, BOTTOM
    }
}