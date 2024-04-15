# DEMO-PRODUCTS System
## This is a simple Spring Boot application for managing products.

Features
CRUD Operations: Perform Create, Read, Update, and Delete operations on products.
RESTful API: Exposes RESTful endpoints for interacting with products.

Getting Started

Prerequisites
Java 17 or higher
Maven

Installation
Clone the repository:
git clone https://github.com/Sstefu/demo-products.git

Navigate to the project directory: cd demo-products

Build project: mvn clean install
Run the application: mvn spring:boot run

# Usage
API Endpoints

GET /api/v1/products: Retrieve all products.

GET /api/v1/products/{id}: Retrieve a product by ID.

POST /api/v1/products: Create a new product.

PUT /api/v1/products/{id}: Update an existing product.

DELETE /api/v1/products/{id}: Delete a product by ID.

PATCH /api/v1/products/{id}: Patch a product by ID 
