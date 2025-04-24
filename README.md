# Examinerande-Projekt-Java
# Kravspecifikation: Webshop-projekt

## Trelagerarkitektur
Projektet ska implementeras med trelagerarkitektur:
- Presentationslager (konsol eller enkel GUI)
- Affärslogiklager (service-klasser)
- Datalager (repository/DAO)

## Krav för Godkänt (G)

### Kunder
- Göra en INSERT till customers-tabellen
- Göra en UPDATE i customers-tabellen. Uppdatera kundens email.

### Produkter
- Visa alla produkter (SELECT * FROM products)
- Söka produkter efter namn (SELECT * FROM products WHERE name LIKE)
- Söka produkter på kategori (SELECT med JOIN på products_categories)
- Uppdatera pris
- Uppdatera lagersaldo (stock_quantity)
- Lägga till nya produkter (INSERT into products)

### Ordrar
- Se orderhistorik för en kund (SELECT med JOIN på orders och customers WHERE customer_id = ?)
- Lägga en order (INSERT into orders)
- Lägga en order med flera produkter (INSERT into orders_products)
- Ange antal av produkten vid orderläggning (Görs tillsammans med ovan INSERT)
- Se totala priset på en order (Räkna ihop priset gånger antalet)

### Validering
- Validering för alla inserts, updates och sökningar (Tex inte kunna ange negativt tal i quantity)
- Särskild validering av lagersaldo vid orderläggning (inte kunna lägga till produkt i order om quantity är större än stock_quantity)

### Felhantering
- Hantera SQL-undantag med tydliga felmeddelanden (vi vill veta vad som gick fel)
- Användarvänliga meddelanden när något går fel
