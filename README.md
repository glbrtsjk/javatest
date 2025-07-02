# Advanced E-Commerce Website

A comprehensive e-commerce application built with **Spring Boot 3.2**, featuring both REST API and web interface capabilities.

## 🚀 Features

### Core E-Commerce Functionality
- **User Authentication & Authorization** - JWT-based security with role-based access (ADMIN/USER)
- **Product Catalog Management** - Complete product listing with categories and advanced search
- **Shopping Cart** - Add/remove items with persistent storage
- **Order Processing** - Full order lifecycle management (PENDING → CONFIRMED → SHIPPED → DELIVERED)
- **Admin Panel** - Comprehensive management interface for products, orders, and users

### Technical Features
- **RESTful API** - Complete REST endpoints for mobile/external integration
- **Web Interface** - Modern responsive UI using Thymeleaf and Bootstrap 5
- **Database Integration** - JPA entities with proper relationships using H2 database
- **Security** - Modern Spring Security 6.x configuration with JWT authentication
- **Search & Filtering** - Advanced product search with category-based filtering
- **Pagination** - Efficient data pagination for large product catalogs

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2, Spring Security 6.x, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, HTML5, CSS3, JavaScript
- **Database**: H2 (in-memory for development)
- **Authentication**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **Java Version**: 17

## 🎯 Quick Start

1. **Clone the repository**
```bash
git clone <repository-url>
cd javatest
```

2. **Run the application**
```bash
mvn spring-boot:run
```

3. **Access the application**
- **Web Interface**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
- **API Base URL**: http://localhost:8080/api

## 🔐 Demo Credentials

- **Admin User**: `admin` / `admin123`
- **Regular User**: `user` / `user123`

## 📱 API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Products
- `GET /api/products` - List all products (with pagination)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?keyword=` - Search products
- `GET /api/products/category/{categoryId}` - Get products by category

### Categories
- `GET /api/categories` - List all categories

### Shopping Cart (Authenticated)
- `GET /api/cart` - View cart
- `POST /api/cart/add` - Add item to cart
- `DELETE /api/cart/remove/{productId}` - Remove item from cart

### Orders (Authenticated)
- `GET /api/orders` - List user orders
- `POST /api/orders` - Create new order

### Admin (Admin Role Required)
- `GET /api/admin/users` - Manage users
- `POST /api/admin/products` - Create products
- `PUT /api/admin/products/{id}` - Update products
- `GET /api/admin/orders` - Manage all orders

## 🌐 Web Interface

### Public Pages
- **Home Page** (`/`) - Featured products and categories
- **Products** (`/products`) - Product catalog with search and filtering
- **Login** (`/login`) - User authentication
- **Register** (`/register`) - User registration

### Features
- Responsive design for mobile and desktop
- Real-time product search
- Category-based filtering
- Professional styling with custom CSS
- Interactive product cards with stock information

## 🗄️ Database Schema

The application uses the following main entities:
- **Users** - User accounts with roles
- **Categories** - Product categories
- **Products** - Product catalog
- **Carts & Cart Items** - Shopping cart functionality
- **Orders & Order Items** - Order management

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

Tests include:
- Application context loading
- Web controller functionality
- Integration tests for key features

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/ecommerce/app/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST and Web controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security components
│   │   └── service/         # Business logic
│   └── resources/
│       ├── static/          # CSS, JS, images
│       ├── templates/       # Thymeleaf templates
│       └── application.properties
└── test/                    # Test classes
```

## 🔧 Configuration

The application comes pre-configured with:
- H2 in-memory database
- Sample data initialization
- JWT token configuration
- Security settings
- Logging configuration

## 📈 Future Enhancements

- Payment integration
- Email notifications
- Advanced inventory management
- Product reviews and ratings
- Order tracking system
- Multi-language support