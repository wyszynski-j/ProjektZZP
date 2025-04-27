# Task Manager App

Aplikacja internetowa do zarządzania zadaniami, stworzona z wykorzystaniem Spring Boot (backend) oraz Angular (frontend). Użytkownicy mogą tworzyć, edytować i usuwać zadania, które są przypisane do nich. Każde zadanie może należeć do określonej kategorii.

## Funkcje aplikacji

### 1. Moduł zarządzania użytkownikami:
- Logowanie użytkownika z wykorzystaniem Spring Security.
- Możliwość rejestracji nowych użytkowników (opcjonalnie).
- Możliwość wylogowania użytkownika.
- Powiązanie danych (kategorii i zadań) z zalogowanym użytkownikiem.

### 2. Moduł zarządzania kategoriami:
- Użytkownicy mogą tworzyć i zarządzać kategoriami.
- Kategorie są unikalne dla każdego użytkownika (ta sama nazwa może występować u różnych użytkowników, ale nie może być duplikowana u jednego).
- Każda kategoria ma nazwę i jest przypisana do użytkownika.

### 3. Moduł zarządzania zadaniami:
- Użytkownicy mogą tworzyć, edytować, zmieniać status zadań oraz usuwać je.
- Zadania zawierają:
  - Tytuł
  - Opis
  - Status (NEW, IN_PROGRESS, COMPLETED)
  - Kategorię
- Zadania są przypisane do konkretnego użytkownika.

### 4. Opcjonalnie: Moduł zarządzania statusami:
- Użytkownicy mogą tworzyć własne statusy, które będą przypisywane do zadań.
- Możliwość edycji i usuwania statusów po walidacji.

### 5. Widoki aplikacji:
- Lista zadań
- Formularz tworzenia i edycji zadań
- Lista kategorii
- Formularz tworzenia i edycji kategorii

## Technologie

- **Backend:**
  - Spring Boot 3
  - Spring Security
  - Spring Data JPA, Hibernate
  - H2 Database (do celów testowych)
  - Liquibase (do zarządzania migracjami bazy danych)
  
- **Frontend:**
  - Angular
  - Bootstrap (dla responsywnych i estetycznych widoków)

- **Bezpieczeństwo:**
  - Spring Security

- **Baza danych:**
  - H2 z wykorzystaniem JPA i Hibernate
  - Liquibase z formatem XML do tworzenia danych inicjalnych
