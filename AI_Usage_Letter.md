# AI Usage Statement - Component-Based Programming Course

**Course:** Component-Based Programming (2026-3)  
**Professor:** Jorge Eduardo Hernández R.  
**Students:** [Your Name / Team Members]  
**Date:** September 6, 2026

---

## Tools Used
In the development of this Mini Golf application, we utilized the following AI assistance:

1.  **Google Gemini (Android Studio Integration):** Used for architecting the project using the MVVM pattern and implementing Jetpack Compose UI components.
2.  **Advanced Logic Assistance:** The logic for sensor integration was optimized by switching from `TYPE_LINEAR_ACCELERATION` to a robust `TYPE_ACCELEROMETER` implementation with a custom **Low-Pass Filter (LPF)** to isolate linear motion from gravity. This ensures compatibility across all Android devices and emulators.
3.  **Performance Optimization:** Assisted in identifying and resolving `SecurityException` related to sensor sampling rates, leading to the implementation of `HIGH_SAMPLING_RATE_SENSORS` permissions for a more responsive gaming experience.
4.  **Unit Testing Generation:** JUnit 4 tests for the `GolfEngine` class were structured following AI-generated boilerplate to ensure robust verification of game rules.

## How AI was Integrated
The AI served as a technical consultant and boilerplate generator. The core design decisions, such as the threshold for swing detection and the specific interaction model for the Android device, were validated and manually adjusted by the students to meet the project requirements.

## Benefits and Impact
- **Productivity:** Significantly reduced the time required to set up the Android project and dependencies.
- **Code Quality:** Helped maintain a clean project structure following modern Android development best practices.
- **Learning:** Assisted in understanding the mapping between raw sensor data and game physics.

---
*This document confirms our transparent use of AI tools to enhance the learning and development process for Taller 01.*
