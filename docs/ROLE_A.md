# Role A (Role 1): Group Leader — System Architect & Core Data Manager

Owns the application backbone and shared data contract, coordinates integration and tracks team decisions. This role develops core Java components; project leadership is not a substitute for a comparable coding contribution.

## Main programming ownership

- `Main.java`: launch the HTML server and provide the shared controller factory used by the Scanner menu.
- `AppController.java`: coordinate navigation and calls between UI, storage, communication logic and AI; validate the active profile/context; catch expected application errors without swallowing them.
- `User.java`, `Communicator.java`, `Supporter.java`: implement the real inheritance hierarchy and overridden role behavior used through a `User` reference in the application.
- `FileManager.java`: own agreed object arrays and separate counters, ID allocation, add/find/update/delete loops, UTF-8 `.txt` persistence, reload, and safe file error handling. Coordinate schemas and method signatures before implementation.

This is a substantial backend module. To keep work balanced, Role A does not own the HTML screens, AI transport/JSON parser, or personalization algorithms.

## Integration and coordination

- Lead agreement on screen flow, class diagram, method signatures, shared branch/repository conventions and merge order.
- Publish the integration contract and decision log; review interfaces with B, C and D before they code against them.
- Resolve integration conflicts with the owning teammate and record the accepted change; do not silently rewrite their module.
- Coordinate final clean build and submission packaging with the team.

## Acceptance evidence

Demonstrate startup and navigation, role-specific overridden behavior through a parent reference, array/counter CRUD, persistence across restart, and clear storage-error handling. Record only evidence actually collected.
