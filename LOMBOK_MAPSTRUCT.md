# Lombok & MapStruct Integration

## Overview

This project uses **Lombok** and **MapStruct** to reduce boilerplate code and simplify object mapping.

## Lombok Annotations Used

### Entity Classes (`User.java`)

```java
@Entity
@Table(name = "users")
@Getter                           // Generates getters for all fields
@Setter                           // Generates setters for all fields
@NoArgsConstructor                // Generates no-args constructor
@AllArgsConstructor               // Generates all-args constructor
@Builder                          // Generates builder pattern
@ToString                         // Generates toString() method
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Custom equals/hashCode
public class User {
    @EqualsAndHashCode.Include     // Include only ID in equals/hashCode
    private UUID id;

    @Builder.Default               // Default value in builder
    private boolean active = true;
}
```

**Benefits:**
- ✅ **60+ lines** of boilerplate code eliminated
- ✅ No more manual getters/setters
- ✅ Built-in builder pattern
- ✅ Proper equals/hashCode implementation
- ✅ Clean toString() method

### DTOs (`UserDTO.java`, `ExternalUserResponse.java`, etc.)

```java
@Data                     // Combines @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor        // No-args constructor for JSON deserialization
@AllArgsConstructor       // All-args constructor
@Builder                  // Builder pattern
public class UserDTO {
    private UUID id;
    private String username;
    // ... other fields
}
```

**Benefits:**
- ✅ **30+ lines** per DTO eliminated
- ✅ Clean, readable code
- ✅ Easy JSON serialization/deserialization
- ✅ Builder pattern for test data

### Services & Controllers

```java
@Service
@RequiredArgsConstructor  // Constructor injection for final fields
@Slf4j                   // Logger instance (log.info(), log.debug(), etc.)
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        log.debug("Creating user: {}", user.getUsername());
        // ... business logic
        log.info("User created with id: {}", savedUser.getId());
    }
}
```

**Benefits:**
- ✅ No manual constructor
- ✅ Automatic dependency injection
- ✅ Built-in logger
- ✅ **10+ lines** per service eliminated

## MapStruct - Object Mapping

### UserMapper Interface

```java
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);
}
```

**Generated Implementation:**
MapStruct automatically generates `UserMapperImpl.java` with:
- Type-safe mapping
- Null safety
- High performance (no reflection)
- Compile-time validation

### Usage in Controller (Future Enhancement)

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;  // Future: Add mapper

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("Creating user: {}", userDTO.getUsername());
        User user = userMapper.toEntity(userDTO);
        User savedUser = userService.createUser(user);
        UserDTO responseDTO = userMapper.toDTO(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
```

**Benefits:**
- ✅ Separation of concerns (Entity vs DTO)
- ✅ No manual field mapping
- ✅ Compile-time safety
- ✅ Better API design
- ✅ Hide internal entity structure

## Configuration

### gradle.properties

```properties
# Dependency Versions
KARATE_JUNIT6_VERSION=2.1.4
MAPSTRUCT_VERSION=1.6.3
LOMBOK_MAPSTRUCT_BINDING_VERSION=0.2.0
```

### build.gradle.kts

```kotlin
dependencies {
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // MapStruct
    implementation("org.mapstruct:mapstruct:${property("MAPSTRUCT_VERSION")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("MAPSTRUCT_VERSION")}")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:${property("MAPSTRUCT_VERSION")}")

    // Lombok + MapStruct integration
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${property("LOMBOK_MAPSTRUCT_BINDING_VERSION")}")
}
```

**Important:** Lombok annotation processor must run **before** MapStruct.

## Code Reduction Summary

### Before (Without Lombok)

**User.java**: ~130 lines
```java
public class User {
    private UUID id;
    private String username;
    // ... 8 fields total

    // No-args constructor: 2 lines
    // All-args constructor: 10 lines
    // 8 getters: 24 lines
    // 8 setters: 24 lines
    // equals(): 20 lines
    // hashCode(): 10 lines
    // toString(): 15 lines

    // Total: ~130 lines
}
```

### After (With Lombok)

**User.java**: ~60 lines
```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String username;

    // ... 6 more fields with annotations

    // Total: ~60 lines (54% reduction!)
}
```

### Before (Without MapStruct)

**Manual Mapping**:
```java
public UserDTO toDTO(User user) {
    if (user == null) {
        return null;
    }

    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setFirstName(user.getFirstName());
    dto.setLastName(user.getLastName());
    dto.setActive(user.isActive());
    dto.setCreatedAt(user.getCreatedAt());
    dto.setUpdatedAt(user.getUpdatedAt());

    return dto;
}

// Total: 16 lines per mapping method
// With 4 methods: 64 lines
```

### After (With MapStruct)

**UserMapper.java**: ~12 lines
```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
    List<UserDTO> toDTOList(List<User> users);
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);
}

// Total: ~12 lines (81% reduction!)
```

## IDE Support

### IntelliJ IDEA

1. Install **Lombok Plugin**:
   - Settings → Plugins → Search "Lombok" → Install
   - Settings → Build → Compiler → Annotation Processors → ✅ Enable annotation processing

2. Install **MapStruct Support** (optional):
   - Settings → Plugins → Search "MapStruct Support" → Install

### VS Code

1. Install **Lombok Annotations Support**:
   ```json
   {
     "java.jdt.ls.vmargs": "-javaagent:/path/to/lombok.jar"
   }
   ```

2. Install **Extension Pack for Java**

## Best Practices

### 1. Use @Data Carefully

```java
// ❌ Don't use @Data on entities (causes issues with Hibernate)
@Entity
@Data  // Bad: generates toString with lazy-loaded fields
public class User { }

// ✅ Use specific annotations for entities
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"lazyField"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private UUID id;
}
```

### 2. Builder Default Values

```java
@Builder
public class User {
    @Builder.Default
    private boolean active = true;  // Default value in builder

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

// Usage:
User user = User.builder()
    .username("john")
    .email("john@example.com")
    .build();  // active = true by default
```

### 3. Immutable DTOs

```java
@Value  // Immutable version of @Data
@Builder
public class UserDTO {
    UUID id;
    String username;
    String email;
}
```

### 4. Custom Mapping with MapStruct

```java
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "firstName", target = "fname")
    @Mapping(source = "lastName", target = "lname")
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + ' ' + user.getLastName())")
    UserDTO toDTO(User user);
}
```

## Generated Files

MapStruct generates implementations in:
```
build/generated/sources/annotationProcessor/java/main/
└── com/example/karateframework/mapper/
    └── UserMapperImpl.java
```

**UserMapperImpl.java** (auto-generated):
```java
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-01T12:00:00+0700"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();
        userDTO.id( user.getId() );
        userDTO.username( user.getUsername() );
        // ... other fields
        return userDTO.build();
    }

    // ... other methods
}
```

## Compilation Order

**Important:** Lombok must run before MapStruct!

```kotlin
// build.gradle.kts
dependencies {
    // 1. Lombok processors (runs first)
    annotationProcessor("org.projectlombok:lombok")

    // 2. MapStruct processors (runs second)
    annotationProcessor("org.mapstruct:mapstruct-processor")

    // 3. Binding to ensure correct order
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
}
```

## Troubleshooting

### Problem: MapStruct can't find getters/setters

**Solution:** Ensure Lombok runs first:
```bash
./gradlew clean compileJava
```

### Problem: IntelliJ shows errors but compiles fine

**Solution:**
1. Enable annotation processing: Settings → Build → Compiler → Annotation Processors
2. Install Lombok plugin
3. Rebuild project: Build → Rebuild Project

### Problem: Generated mapper not found

**Solution:**
```bash
# Clean and rebuild
./gradlew clean compileJava

# Check generated files
ls -la build/generated/sources/annotationProcessor/java/main/
```

## Summary

### Lines of Code Reduced

| File | Without Lombok/MapStruct | With Lombok/MapStruct | Reduction |
|------|-------------------------|----------------------|-----------|
| User.java | ~130 lines | ~60 lines | 54% |
| UserDTO.java | ~100 lines | ~35 lines | 65% |
| UserMapper.java | ~64 lines | ~12 lines | 81% |
| UserService.java | ~120 lines | ~100 lines | 17% |
| UserController.java | ~110 lines | ~95 lines | 14% |
| **Total** | **~524 lines** | **~302 lines** | **42%** |

### Key Benefits

✅ **42% code reduction** overall
✅ **Zero runtime overhead** (compile-time code generation)
✅ **Type-safe** mapping (compile-time validation)
✅ **Better maintainability** (less code to maintain)
✅ **Improved readability** (focus on business logic)
✅ **Built-in logging** with @Slf4j
✅ **Constructor injection** with @RequiredArgsConstructor
✅ **Separation of concerns** (Entity vs DTO)

### Version Information

- Lombok: Managed by Spring Boot (latest)
- MapStruct: 1.6.3
- Lombok-MapStruct Binding: 0.2.0

All versions are externalized in `gradle.properties` for easy updates.
