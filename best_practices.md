# Professional Development Standards for Enterprise Applications

## 1. SOLID Principles Implementation

### 1.1 Single Responsibility Principle (SRP)
Each class should have only one reason to change. Separate concerns into distinct components.

```java
// ❌ BAD: Multiple responsibilities
@Service
public class UserService {
    public void createUser(User user) { }
    public void sendWelcomeEmail(User user) { }
    public void logUserCreation(User user) { }
}

// ✅ GOOD: Separated responsibilities
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
}

@Service
public class EmailService {
    public void sendWelcomeEmail(User user) {
        // Email logic
    }
}

@Service
public class AuditService {
    public void logUserCreation(User user) {
        // Logging logic
    }
}
```

### 1.2 Open-Closed Principle (OCP)
Classes should be open for extension but closed for modification.

```java
// Strategy Pattern implementation
public interface PaymentProcessor {
    void processPayment(BigDecimal amount);
}

@Component
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(BigDecimal amount) {
        // Credit card processing logic
    }
}

@Component
public class PayPalProcessor implements PaymentProcessor {
    @Override
    public void processPayment(BigDecimal amount) {
        // PayPal processing logic
    }
}

@Service
public class PaymentService {
    private final Map<String, PaymentProcessor> processors;
    
    @Autowired
    public PaymentService(Map<String, PaymentProcessor> processors) {
        this.processors = processors;
    }
    
    public void process(String type, BigDecimal amount) {
        processors.get(type).processPayment(amount);
    }
}
```

### 1.3 Liskov Substitution Principle (LSP)
Subclasses must be substitutable for their base classes without altering program correctness.

```java
// ✅ GOOD: Proper abstraction
public abstract class Notification {
    abstract void send(String message);
}

public class EmailNotification extends Notification {
    @Override
    void send(String message) {
        // Email implementation
    }
}

public class SMSNotification extends Notification {
    @Override
    void send(String message) {
        // SMS implementation
    }
}
```

### 1.4 Interface Segregation Principle (ISP)
Clients should not be forced to depend on interfaces they don't use.

```java
// ❌ BAD: Fat interface
public interface UserOperations {
    void createUser();
    void deleteUser();
    void updateUser();
    void sendEmail();
    void generateReport();
}

// ✅ GOOD: Segregated interfaces
public interface UserManagement {
    void createUser();
    void updateUser();
    void deleteUser();
}

public interface UserCommunication {
    void sendEmail();
}

public interface UserReporting {
    void generateReport();
}
```

### 1.5 Dependency Inversion Principle (DIP)
Depend on abstractions, not concretions.

```java
// Repository abstraction
public interface ProductRepository {
    Product findById(Long id);
    List<Product> findAll();
}

@Service
public class ProductService {
    private final ProductRepository repository;
    
    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    public Product getProduct(Long id) {
        return repository.findById(id);
    }
}
```

---

## 2. Inversion of Control (IoC) & Dependency Injection

### 2.1 Constructor Injection (Recommended)
```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    @Autowired
    public OrderService(PaymentService paymentService, 
                       InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }
}
```

### 2.2 Configuration Classes
```java
@Configuration
@EnableTransactionManagement
public class ApplicationConfig {
    
    @Bean
    @Profile("production")
    public DataSource productionDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://prod-server:5432/db")
            .username("produser")
            .password("${db.password}")
            .build();
    }
    
    @Bean
    @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new CaffeineCacheManager();
    }
}
```

---

## 3. Project Structure & Architecture

### 3.1 Layered Architecture
```
src/main/java/com/company/project/
├── controller/        # REST endpoints
├── service/          # Business logic
├── repository/       # Data access
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── mapper/          # Object mappers
├── exception/       # Custom exceptions
├── config/          # Configuration classes
├── security/        # Security components
└── util/           # Utility classes
```

### 3.2 Package by Feature (Alternative)
```
src/main/java/com/company/project/
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   └── UserDTO.java
├── product/
│   ├── ProductController.java
│   ├── ProductService.java
│   └── ProductRepository.java
└── common/
    ├── config/
    └── exception/
```

---

## 4. Testing Strategies

### 4.1 Test-Driven Development (TDD)
```java
// 1. Write test first
@Test
void shouldCalculateTotalPrice() {
    // Given
    OrderService orderService = new OrderService();
    List<Item> items = Arrays.asList(
        new Item("Book", 10.00, 2),
        new Item("Pen", 2.50, 3)
    );
    
    // When
    double total = orderService.calculateTotal(items);
    
    // Then
    assertEquals(27.50, total);
}

// 2. Implement minimum code to pass
public double calculateTotal(List<Item> items) {
    return items.stream()
        .mapToDouble(item -> item.getPrice() * item.getQuantity())
        .sum();
}
```

### 4.2 Unit Testing
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUser() {
        // Given
        User user = new User("John", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        User created = userService.createUser(user);
        
        // Then
        assertNotNull(created);
        verify(userRepository, times(1)).save(user);
    }
}
```

### 4.3 Integration Testing
```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }
}
```

### 4.4 Repository Testing
```java
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindByEmail() {
        // Given
        User user = new User("John", "john@example.com");
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
}
```

---

## 5. RESTful API Design

### 5.1 Controller Best Practices
```java
@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }
    
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, 
                                  @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

### 5.2 Global Exception Handling
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .validationErrors(errors)
            .build();
        return ResponseEntity.badRequest().body(error);
    }
}
```

---

## 6. Data Access & JPA

### 6.1 Repository Pattern
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") UserStatus status);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("lastLogin") LocalDateTime lastLogin);
}
```

### 6.2 Entity Design
```java
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    // Getters and setters
}
```

---

## 7. Security Best Practices

### 7.1 JWT Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(unauthorizedHandler())
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 7.2 Method Security
```java
@Service
public class AdminService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // Admin only operation
    }
    
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public UserDTO getUser(Long userId) {
        // User can access own data or admin can access any
    }
    
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long documentId) {
        // Verify ownership after retrieval
    }
}
```

---

## 8. Microservices Patterns

### 8.1 Service Discovery with Eureka
```java
@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

### 8.2 Circuit Breaker Pattern
```java
@Service
public class PaymentService {
    
    @CircuitBreaker(name = "payment", fallbackMethod = "paymentFallback")
    @Retry(name = "payment")
    @Bulkhead(name = "payment")
    public PaymentResponse processPayment(PaymentRequest request) {
        // External payment service call
        return paymentClient.process(request);
    }
    
    public PaymentResponse paymentFallback(PaymentRequest request, Exception ex) {
        return PaymentResponse.builder()
            .status("PENDING")
            .message("Payment service temporarily unavailable")
            .build();
    }
}
```

### 8.3 API Gateway Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/products/**
          filters:
            - AddRequestHeader=X-Request-Id, #{T(java.util.UUID).randomUUID().toString()}
            - CircuitBreaker=productService
```

---

## 9. Performance & Optimization

### 9.1 Caching
```java
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    @CachePut(value = "products", key = "#product.id")
    public Product update(Product product) {
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    @Scheduled(fixedDelay = 3600000) // Clear cache every hour
    public void clearCache() {
        // Cache cleanup
    }
}
```

### 9.2 Database Query Optimization
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH o.customer c " +
           "WHERE o.status = :status")
    List<Order> findByStatusWithDetails(@Param("status") OrderStatus status);
    
    @EntityGraph(attributePaths = {"items", "customer"})
    Optional<Order> findWithDetailsById(Long id);
}
```

---

## 10. Configuration Management

### 10.1 Application Properties
```yaml
# application.yml
spring:
  application:
    name: product-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/productdb
    username: ${DB_USERNAME:defaultuser}
    password: ${DB_PASSWORD:defaultpass}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        show_sql: false
        format_sql: true
        jdbc:
          batch_size: 20

logging:
  level:
    root: INFO
    com.company: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus
```

### 10.2 Profiles
```java
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    
    @Override
    public void run(String... args) {
        // Initialize data for non-test environments
    }
}
```

---

## 11. Logging & Monitoring

### 11.1 Structured Logging
```java
@RestController
@Slf4j
public class OrderController {
    
    @PostMapping("/orders")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        try {
            Order order = orderService.create(request);
            log.info("Order created successfully with ID: {}", order.getId());
            return OrderResponse.from(order);
        } catch (Exception e) {
            log.error("Failed to create order for customer: {}", 
                     request.getCustomerId(), e);
            throw new OrderCreationException("Order creation failed", e);
        }
    }
}
```

### 11.2 Custom Metrics
```java
@Component
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void recordOrderProcessingTime(long duration) {
        meterRegistry.timer("order.processing.time").record(duration, TimeUnit.MILLISECONDS);
    }
    
    public void incrementOrderCount(String status) {
        meterRegistry.counter("order.count", "status", status).increment();
    }
}
```

---

## 12. Documentation

### 12.1 OpenAPI/Swagger Configuration
```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Service API")
                .version("1.0")
                .description("Product management REST API")
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0")))
            .addSecurityItem(new SecurityRequirement()
                .addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme("bearer")));
    }
}
```

### 12.2 API Documentation
```java
@RestController
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {
    
    @Operation(summary = "Get product by ID", 
               description = "Returns a single product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Product not found")
    })
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }
}
```

---

## 13. Development Tools & Practices

### 13.1 Code Quality Tools
```xml
<!-- Maven plugins for code quality -->
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>3.9.1.2184</version>
</plugin>

<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 13.2 Git Commit Convention
```
feat: Add user authentication
fix: Resolve null pointer in order service
docs: Update API documentation
refactor: Extract email service from user service
test: Add integration tests for payment flow
chore: Update dependencies
```

---

## 14. Common Anti-Patterns to Avoid

### 14.1 God Classes
Avoid creating classes with too many responsibilities. Split them according to SRP.

### 14.2 Anemic Domain Model
Ensure entities have behavior, not just getters and setters.

### 14.3 Tight Coupling
Use interfaces and dependency injection to reduce coupling.

### 14.4 Ignoring Transaction Boundaries
```java
@Service
@Transactional
public class OrderService {
    // Ensure proper transaction management
}
```

### 14.5 N+1 Query Problem
Use appropriate fetching strategies to avoid performance issues.
