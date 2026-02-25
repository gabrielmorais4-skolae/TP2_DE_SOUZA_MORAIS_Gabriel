#!/bin/bash
BASE="http://localhost:8080/api"

echo "=== 1. CATEGORIES ==="
CAT1=$(curl -s -X POST "$BASE/categories" \
  -H "Content-Type: application/json" \
  -d '{"name":"Électronique"}' | jq -r '.id')
echo "Électronique → $CAT1"

CAT2=$(curl -s -X POST "$BASE/categories" \
  -H "Content-Type: application/json" \
  -d '{"name":"Informatique"}' | jq -r '.id')
echo "Informatique → $CAT2"

CAT3=$(curl -s -X POST "$BASE/categories" \
  -H "Content-Type: application/json" \
  -d '{"name":"Mobilier"}' | jq -r '.id')
echo "Mobilier → $CAT3"

echo ""
echo "=== 2. SUPPLIERS ==="
SUP1=$(curl -s -X POST "$BASE/suppliers" \
  -H "Content-Type: application/json" \
  -d '{"name":"TechPro","email":"contact@techpro.fr","phone":"0600000001"}' | jq -r '.id')
echo "TechPro → $SUP1"

SUP2=$(curl -s -X POST "$BASE/suppliers" \
  -H "Content-Type: application/json" \
  -d '{"name":"MegaDistrib","email":"info@megadistrib.fr","phone":"0600000002"}' | jq -r '.id')
echo "MegaDistrib → $SUP2"

echo ""
echo "=== 3. PRODUCTS ==="
P1=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"iPhone 15\",\"description\":\"Smartphone Apple\",\"price\":1199.99,\"categoryId\":\"$CAT1\",\"supplierId\":\"$SUP1\",\"stockQuantity\":50}" | jq -r '.id')
echo "iPhone 15 → $P1"

P2=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Samsung Galaxy S24\",\"description\":\"Smartphone Samsung\",\"price\":899.99,\"categoryId\":\"$CAT1\",\"supplierId\":\"$SUP1\",\"stockQuantity\":30}" | jq -r '.id')
echo "Samsung S24 → $P2"

P3=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"AirPods Pro\",\"description\":\"Écouteurs sans fil\",\"price\":279.99,\"categoryId\":\"$CAT1\",\"supplierId\":\"$SUP2\",\"stockQuantity\":100}" | jq -r '.id')
echo "AirPods Pro → $P3"

P4=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"MacBook Pro M3\",\"description\":\"Laptop Apple 14 pouces\",\"price\":2499.00,\"categoryId\":\"$CAT2\",\"supplierId\":\"$SUP1\",\"stockQuantity\":20}" | jq -r '.id')
echo "MacBook Pro → $P4"

P5=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Dell XPS 15\",\"description\":\"Laptop Dell\",\"price\":1799.00,\"categoryId\":\"$CAT2\",\"supplierId\":\"$SUP2\",\"stockQuantity\":15}" | jq -r '.id')
echo "Dell XPS 15 → $P5"

P6=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Clavier Mécanique\",\"description\":\"Clavier gaming RGB\",\"price\":149.99,\"categoryId\":\"$CAT2\",\"supplierId\":\"$SUP2\",\"stockQuantity\":75}" | jq -r '.id')
echo "Clavier Mécanique → $P6"

P7=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Bureau Ergonomique\",\"description\":\"Bureau réglable en hauteur\",\"price\":599.00,\"categoryId\":\"$CAT3\",\"supplierId\":\"$SUP2\",\"stockQuantity\":10}" | jq -r '.id')
echo "Bureau Ergonomique → $P7"

# Produit jamais commandé (pour tester /stats/never-ordered)
P8=$(curl -s -X POST "$BASE/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Chaise Gaming\",\"description\":\"Chaise gamer pro\",\"price\":399.00,\"categoryId\":\"$CAT3\",\"supplierId\":\"$SUP1\",\"stockQuantity\":5}" | jq -r '.id')
echo "Chaise Gaming (jamais commandée) → $P8"

echo ""
echo "=== 4. ORDERS ==="
O1=$(curl -s -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d "{\"customerName\":\"Alice Martin\",\"customerEmail\":\"alice@example.com\",\"productsAndQuantities\":{\"$P1\":2,\"$P3\":1}}" | jq -r '.id')
echo "Order 1 → $O1"

O2=$(curl -s -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d "{\"customerName\":\"Bob Dupont\",\"customerEmail\":\"bob@example.com\",\"productsAndQuantities\":{\"$P4\":1,\"$P6\":2}}" | jq -r '.id')
echo "Order 2 → $O2"

O3=$(curl -s -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d "{\"customerName\":\"Clara Bernard\",\"customerEmail\":\"clara@example.com\",\"productsAndQuantities\":{\"$P1\":1,\"$P5\":1}}" | jq -r '.id')
echo "Order 3 → $O3"

O4=$(curl -s -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d "{\"customerName\":\"David Chen\",\"customerEmail\":\"david@example.com\",\"productsAndQuantities\":{\"$P2\":3,\"$P3\":2}}" | jq -r '.id')
echo "Order 4 → $O4"

O5=$(curl -s -X POST "$BASE/orders" \
  -H "Content-Type: application/json" \
  -d "{\"customerName\":\"Eva Torres\",\"customerEmail\":\"eva@example.com\",\"productsAndQuantities\":{\"$P4\":1}}" | jq -r '.id')
echo "Order 5 → $O5"

echo ""
echo "=== 5. MISE À JOUR STATUTS (pour totalRevenue) ==="
curl -s -X PUT "$BASE/orders/$O1/status" \
  -H "Content-Type: application/json" \
  -d '{"status":"DELIVERED"}' | jq '{id:.id, status:.status}'

curl -s -X PUT "$BASE/orders/$O2/status" \
  -H "Content-Type: application/json" \
  -d '{"status":"DELIVERED"}' | jq '{id:.id, status:.status}'

curl -s -X PUT "$BASE/orders/$O3/status" \
  -H "Content-Type: application/json" \
  -d '{"status":"SHIPPED"}' | jq '{id:.id, status:.status}'

curl -s -X PUT "$BASE/orders/$O4/status" \
  -H "Content-Type: application/json" \
  -d '{"status":"CONFIRMED"}' | jq '{id:.id, status:.status}'

# O5 reste PENDING

echo ""
echo "=== DONE — testez maintenant les endpoints stats ==="
echo "GET $BASE/products/stats/category-stats"
echo "GET $BASE/products/stats/count-by-category"
echo "GET $BASE/products/stats/avg-price-by-category"
echo "GET $BASE/products/stats/top-expensive?limit=3"
echo "GET $BASE/products/stats/never-ordered"
echo "GET $BASE/products/stats/categories-with-min-products?min=3"
echo "GET $BASE/orders/stats/total-revenue"
echo "GET $BASE/orders/stats/status-stats"
echo "GET $BASE/orders/stats/most-ordered-products?limit=5"
