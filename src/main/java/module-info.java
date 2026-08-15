module com.example.otelsistemi {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.base;

    opens com.example.Application to javafx.fxml;
    opens com.example.Controllers to javafx.fxml;
    opens com.example.Controllers.Musteri to javafx.fxml;
    opens com.example.Controllers.Yonetici to javafx.fxml;
    opens com.example.Controllers.Admin to javafx.fxml;
    opens com.example.Models to javafx.base;
    
    exports com.example.Application;
    exports com.example.Controllers;
    exports com.example.Controllers.Musteri;
    exports com.example.Controllers.Yonetici;
    exports com.example.Controllers.Admin;
    exports com.example.Models;
}