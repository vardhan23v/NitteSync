# NitteSync

Campus event management platform for Nitte — a Java and Spring Framework Lab
mini-project built with **Jakarta Faces 4.0 (JSF), JDBC and MySQL**.

Students browse and register for events, clubs create and submit events, and
administrators approve or reject them.

---

## 1. Technology stack

| Component | Version | Note |
|---|---|---|
| Java | 21 | `maven.compiler.release` |
| Jakarta Faces API | 4.0.1 | compile only |
| Mojarra (JSF implementation) | 4.0.27 | packaged into `WEB-INF/lib` |
| CDI API | 4.0.1 | compile only |
| Weld Servlet | 5.1.7.Final | CDI implementation for Tomcat |
| Servlet API | 6.0.0 | provided by Tomcat |
| MySQL Connector/J | 8.4.0 | JDBC driver |
| Apache Tomcat | 10.1.x | Servlet 6.0 container |
| Maven | 3.8+ | build |

**Jakarta Faces 4.0 removed `@ManagedBean`**, so every backing bean is a CDI
bean (`@Named` + a scope). Tomcat has no CDI container of its own, which is why
Weld ships inside the WAR and `WEB-INF/beans.xml` exists.

---

## 2. Project layout

```
NitteSync/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/nittesync/
    │   ├── model/      User, Event, Registration
    │   ├── dao/        UserDAO, EventDAO, RegistrationDAO        (JDBC)
    │   ├── service/    AuthService, EventService, RegistrationService (rules)
    │   ├── bean/       JSF backing beans (@Named)
    │   ├── filter/     AuthFilter (login + role gate)
    │   └── util/       DBConnection, PasswordUtil, SessionUtil, FacesUtil,
    │                   DataAccessException, TestConnection
    ├── resources/      db.properties, schema.sql, sample-data.sql
    └── webapp/
        ├── index.xhtml, login.xhtml, dashboard.xhtml, events.xhtml,
        │   event-details.xhtml, my-registrations.xhtml, club-events.xhtml,
        │   event-form.xhtml, admin-approval.xhtml, admin-events.xhtml
        ├── resources/css/style.css
        └── WEB-INF/    web.xml, beans.xml, faces-config.xml, templates/layout.xhtml
```

Layers: **XHTML page → backing bean → service → DAO → JDBC → MySQL**.

The user interface follows the design direction recorded in
[`docs/design.md`](docs/design.md): the *Citrus* theme (tangerine action on warm
off-white, aubergine ink), with the palette, type scale, spacing, radii, shadows
and motion tokens defined once in `src/main/webapp/resources/css/style.css`.
Components use those tokens only, and the palette is contrast-checked.

---

## 3. Set up MySQL (macOS)

```bash
brew services start mysql          # or: mysql.server start
mysql -u root                      # Homebrew's root has no password by default
```

Create the database and load the sample data (run from the project folder):

```bash
mysql -u root -p < src/main/resources/schema.sql
mysql -u root -p < src/main/resources/sample-data.sql
```

Check it:

```bash
mysql -u root -p -e "SELECT id,name,role FROM nitte_sync.users;"
```

If your root account has a password, use `-p` above **and** put the same
password in `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/nitte_sync?sslMode=DISABLED&allowPublicKeyRetrieval=true
db.user=root
db.password=yourPassword
```

`db.properties` is the only place where credentials live.

**Test the connection** — in Eclipse right click `com.nittesync.util.TestConnection`
→ *Run As → Java Application*. It prints the MySQL version, or a readable
explanation of what is wrong.

---

## 4. Import and run in Eclipse

1. **File → Import → Maven → Existing Maven Projects**, choose this folder.
2. Wait for Maven to download the dependencies.
3. Project properties → **Project Facets**: Java **21**, Dynamic Web Module **6.0**.
4. **Window → Preferences → Server → Runtime Environments → Add → Apache Tomcat
   v10.1** and point it at your Tomcat 10.1 folder (JDK 21).
5. Right click the project → **Run As → Run on Server**.

Or without Eclipse's server adapter (always works):

```bash
mvn clean package
cp target/nittesync.war /path/to/apache-tomcat-10.1.x/webapps/
/path/to/apache-tomcat-10.1.x/bin/startup.sh
```

Then open **http://localhost:8080/nittesync/**

### Demo accounts

| Role | Email | Password |
|---|---|---|
| Administrator | admin@nitte.edu.in | password |
| Club | coding.club@nitte.edu.in | password |
| Club | cultural.club@nitte.edu.in | password |
| Student | rahul@nitte.edu.in | password |
| Student | ananya@nitte.edu.in | password |

---

## 5. Troubleshooting

| Symptom | Cause and fix |
|---|---|
| `#{loginBean} not found` / no CDI beans | Weld or `beans.xml` missing. Check that `target/nittesync/WEB-INF/lib` contains `weld-servlet-shaded-5.1.7.Final.jar` and `jakarta.faces-4.0.27.jar`. |
| `ClassNotFoundException: jakarta.faces...` | Mojarra is not inside the WAR. Do **not** copy it into Tomcat's `lib/`. |
| `Public Key Retrieval is not allowed` | Add `allowPublicKeyRetrieval=true&sslMode=DISABLED` to `db.url`. |
| `Access denied for user 'root'@'localhost'` | Wrong `db.user` / `db.password` in `db.properties`. |
| `Communications link failure` | MySQL is not running: `brew services start mysql`. |
| The page shows raw XHTML | The page was opened outside the FacesServlet mapping; use `*.xhtml` URLs. |
| Maven errors in Eclipse | Right click the project → **Maven → Update Project** (Alt+F5) with *Force Update*, then **Project → Clean**. Still stuck: `mvn -U clean package` in a terminal. |
| `WELD-000072: Bean declaring a passivating scope` | A `@ViewScoped` bean must implement `Serializable` (see `EventFormBean`). |

---

## 6. What the application does

* **Student** — see approved events, open the details, register once per event,
  see the events registered for.
* **Club** — create an event (starts as DRAFT), edit or delete its own events
  while they are not approved, submit them for approval.
* **Administrator** — see events waiting for approval, approve or reject them,
  see every event and every registration.

Event status lifecycle: `DRAFT → PENDING → APPROVED | REJECTED`.
Editing an event puts it back to `DRAFT`. Students only ever see `APPROVED`
events. Every role rule is checked on the server (services and `AuthFilter`),
not only by hiding buttons.

## 7. Not implemented

Self-registration of accounts, password reset, e-mail notification, file
uploads, event capacity limits, cancelling a registration, search, pagination,
OAuth/JWT, Spring, Hibernate/JPA. All out of scope for the lab project.