# Incidents – Communal Incident Reporting

Android with Jetpack Compose app for reporting and tracking public incidents.
Many thanks to [Gerjan van den Oever] (https://github.com/gerjanvdoever) for the initial Compose implementation.
This project works in combination with the [Incidents REST API](https://github.com/pjfmast/ktor-incident-api)

## Key Points
- Mobile client built in Jetpack Compose (Android).  
- Communicates with a shared REST API backend.  
- Role-based access and secure user authentication.  
- Streamlined UI for reporting and tracking incidents.  

## Goal
To enhance communication between citizens and municipal services, enabling efficient, transparent handling of public incidents.


## Technology Stack

### Core
- **Kotlin** 2.2.21
- **Android SDK** 36 (minimum SDK 33)

### UI Framework
- **Jetpack Compose** (BOM 2025.12.00)
- **Material 3** 1.4.0
- **Material Symbols and Icons as recommended here: https://developer.android.com/jetpack/androidx/releases/compose-material3#1.4.0**
- **Coil** 2.7.0 - Image loading

### Architecture & Dependencies
- **Koin** 4.2.0-alpha3 - Dependency injection
- **Navigation3** 1.0.0 - Navigation framework
- **Lifecycle & ViewModel** 2.10.0

### Networking
- **Ktor Client** 3.3.3
    - CIO engine
    - Content negotiation
- **Kotlinx Serialization** 1.9.0

### Data & Storage
- **DataStore Preferences** 1.2.0
- **Kotlinx DateTime** 0.7.1

### Maps & Location
- **MapLibre Compose** 0.12.1
- **Google Play Services Location** 21.3.0
