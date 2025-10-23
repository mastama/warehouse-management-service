# Warehouse Management Service (Spring Boot 3 + Postgres + GraalVM Native)

Backend sederhana untuk manajemen gudang toko: **Item**, **Variant**, **Order** (SalesOrder) dengan pencegahan oversell.

## Fitur Utama
- CRUD **Item** & **Variant**
- **Harga** per variant
- **Stok** per variant
- **Pencegahan oversell** saat membuat order (pessimistic lock)
- **Response envelope konsisten**:
  ```json
  { "responseCode": "2010000001", "responseDesc": "Created", "data": { ... } }

## Teknologi
- Java 21 (GraalVM 21 kompatibel)
- Spring Boot 3.x, Spring Web, Spring Data JPA (Hibernate)
- PostgreSQL
- Lombok
- (Opsional) Native Image via GraalVM

## Arsitektur & Keputusan Desain
Layered: controller → service → repository.
DTO (request/response) terpisah dari entity untuk menjaga boundary.

### Pencegahan oversell: saat membuat order
- Lock baris Variant dengan PESSIMISTIC_WRITE (findByIdForUpdate).
- Validasi stok cukup untuk semua line sebelum mengurangi.
- Kurangi stok dan simpan order dalam 1 transaksi untuk atomicity.

### Response Envelope
Semua respons (termasuk error) menggunakan struktur:
- responseCode = HTTP(3 digit) + serviceId(4 digit) + appCode(3 digit)
- Contoh: 201 0000 001 ⇒ 2010000001
- Kode aplikasi (contoh utama):
  - 000 = Approved (200)
  - 001 = Created (201)
  - 002 = No Content (204)
  - 014 = Data tidak ditemukan (404)
  - 017 = Data sudah ada (409)
  - 030 = Format Data Salah (400)
  - 098 = General Error (500)

Delete: untuk tetap mengirim body (data: null), HTTP status dikirim 200 OK, namun responseCode tetap menyertakan segmen 204 (sesuai kebutuhan non-standar).

## Asumsi
- Satu Item memiliki banyak Variant.
- Harga disimpan di Variant (bisa berbeda per varian).
- Order hanya memotong stok varian, tidak mengembalikannya (non-return).
- Validasi minimal untuk contoh (nama wajib ada, SKU wajib ada, qty > 0).
- service.id 4 digit (default 0000).

## ER Model (ringkas)
- Item(id, name, description, createdAt, updatedAt)
- Variant(id, item_id, sku, size, color, price, stock, version)
- SalesOrder(id, createdAt, grandTotal)
- SalesOrderLine(id, order_id, variant_id, quantity, unitPrice, lineTotal)

## Sequence Diagram - Create Order
sequenceDiagram
    participant C as Client
    participant API as OrderController
    participant S as OrderService
    participant VR as VariantRepo
    participant OR as OrderRepo

    C->>API: POST /api/orders (lines)
    API->>S: create(req)
    S->>VR: findByIdForUpdate(variantId...) (lock)
    VR-->>S: Variant...
    S->>S: cek stok cukup untuk semua line
    alt stok tidak cukup
        S->>API: throw IllegalStateException("Out of stock ...")
        API-->>C: 400 (responseCode 400...030, data:null)
    else stok cukup
        S->>S: kurangi stok & hitung total
        S->>OR: save(order + lines)
        OR-->>S: order persisted
        API-->>C: 201 (responseCode 201...001, data:OrderResponse)
    end
# Jalankan Service
1. Clone Repo
2. Java 21 (GraalVM 21 opsional untuk native build)
3. Maven 3.9+
4. PostgreSQL 14+ terpasang lokal

## Konfigurasi Aplikasi
Buat/cek src/main/resources/application.properties:

# ---- Database ----
- spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
- spring.datasource.username=yourpostgresusername
- spring.datasource.password=yourpostgrespassword
- spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.open-in-view=false
- spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# App
- service.id=0000
- app.expose-exception-class=false
- server.port=8080

## Cara Menjalankan
Mode JVM (Untuk Dev)
- mvn -DskipTests spring-boot:run
Aplikasi akan aktif di http://localhost:8080.

## (Opsional) Build Native (GraalVM)
1. Pastikan pakai GraalVM 21:
  - java -version
  - native-image --version
2. (Opsional) generate config refleksi:
  - java -agentlib:native-image-agent=config-output-dir={urlPathDimanaKamuSimpanRepoIni}/warehouse-management-service/src/main/resources/META-INF/native-image -jar target/warehouse-management-service-0.0.1-SNAPSHOT.jar

# --- Endpoint & Contoh Request (curl) ---
## ITEM:
**Create:**
- curl --location 'http://localhost:8080/api/items' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Smart Watch Pertamax",
    "description": "Smart Watch Versi Pertamax"
}'

**Get By Id:**
- curl --location 'http://localhost:8080/api/items/f903fc84-fdf9-4ac3-93a2-1e1deb117718'

**Update:**
- curl --location --request PUT 'http://localhost:8080/api/items/f903fc84-fdf9-4ac3-93a2-1e1deb117718' \
--header 'Content-Type: application/json' \
--data '{
    "name": "Kemeja Merah Maroon update",
    "description": "Kemeja merah maroon naga emas update"
}'

**Delete:**
- curl --location --request DELETE 'http://localhost:8080/api/items/8b24941f-d439-4116-ba70-9a0d0ca9b62f'

## VARIANT:
**Create:**
- curl --location 'localhost:8080/api/items/30bf2e42-ae59-4e02-bf11-5d954ff7cc08/variants' \
--header 'Content-Type: application/json' \
--data '{
    "sku": "SM-WHT-L",
    "size": "L",
    "color": "White",
    "price": 3350000,
    "stock": 10
}'

**Get By Item Id:**
- curl --location 'localhost:8080/api/items/a01cc0df-f64e-4355-94c3-86ba0885df71/variants'

**Get By Id Variant:**
- curl --location 'localhost:8080/api/variants/8d9890de-bba9-4436-9b25-dd4c360fadd1'

**Update:**
- curl --location --request PUT 'localhost:8080/api/variants/8d9890de-bba9-4436-9b25-dd4c360fadd1' \
--header 'Content-Type: application/json' \
--data '{
    "sku": "HD-GRY-L",
    "size": "L",
    "color": "Grey",
    "price": 209000,
    "stock": 10
}'

**Delete:**
- curl --location --request DELETE 'localhost:8080/api/variants/8d9890de-bba9-4436-9b25-dd4c360fadd1'

## Order:
**Create Order 1 lines:**
- curl --location 'localhost:8080/api/orders' \
--header 'Content-Type: application/json' \
--data '{
    "lines": [
        {
            "variantId": "a49ca6a8-5593-4686-9e78-4b6ff8c77b90",
            "quantity": 11
        }
    ]
}'

**Create Order 2 lines:**
- curl --location 'localhost:8080/api/orders' \
--header 'Content-Type: application/json' \
--data '{
    "lines": [
        {
            "variantId": "003f8e89-9446-4f3a-aabc-7ee2b3ce2d9f",
            "quantity": 20
        },
        {
            "variantId": "a49ca6a8-5593-4686-9e78-4b6ff8c77b90",
            "quantity": 1
        }
    ]
}'

**Create Order 5 lines:**
- curl --location 'http://localhost:8080/api/orders' \
--header 'Content-Type: application/json' \
--data '{
    "lines": [
        {
            "variantId": "VARIANT_ID_1",
            "quantity": 2
        },
        {
            "variantId": "VARIANT_ID_2",
            "quantity": 1
        },
        {
            "variantId": "VARIANT_ID_3",
            "quantity": 5
        },
        {
            "variantId": "VARIANT_ID_4",
            "quantity": 2
        },
        {
            "variantId": "VARIANT_ID_5",
            "quantity": 3
        }
    ]
}'

# Lisensi
MIT

## For any question about this service.
### please contact or reach me via LinkedIn: https://linkedin.com/in/singgih-pratama
