## 🛒 Basket Service – Online Sushi Shop
# 📖 Overview
The Basket Service is a microservice responsible for managing the shopping cart in the Online Sushi Shop application.
It provides REST API endpoints for retrieving and updating carts, while integrating with the Catalog Service (via gRPC) to fetch product details and prices.
Cart data is stored in Redis.
# 🏗 Tech Stack
- Golang – service implementation
- Gin – HTTP web framework
- Redis – storage for shopping cart data
- gRPC – communication with Catalog Service
- Swagger (swaggo) – API documentation
# ⚙️ Requirements
- Go 1.21+
- Redis 7+
- Docker & Docker Compose (optional)
# ▶️ Running the Service
Locally
- go mod tidy
- go run main.go
### With Docker
The file docker-compose.yml is located in the path ../../docker-config/docker-compose.yml
# 📡 REST API
### Base Path
- /api/v1/cart
### Endpoints
- GET /
Retrieve the current user's cart.
Response 200:
```json
{
  "id": "string",
  "owner_id": "string",
  "cart_items": [
    {
      "id": "string",
      "product_id": "string",
      "quantity": 1,
      "price": "12.50",
      "details": {
        "name": "Sushi Set",
        "link": "http://...",
        "image_url": "http://..."
      }
    }
  ],
  "total_price": "25.00"
}
```
-  PUT /
Update the current user's cart.
Request body:
```json
{
  "items": [
    {
      "product_id": "12345",
      "quantity": 2
    },
    {
      "product_id": "67890",
      "quantity": 1
    }
  ]
}```
Response 200: Returns the updated cart (same schema as GET /).
# 📑 API Documentation
### Swagger UI is available at:
/api/v1/cart/docs/index.html
(only enabled in non-production environments)
# 🧪 Tests
### Unit tests use:
testify/suite for test suites
httptest for mocking HTTP requests
miniredis for in-memory Redis
Run tests with:
go test ./... -v