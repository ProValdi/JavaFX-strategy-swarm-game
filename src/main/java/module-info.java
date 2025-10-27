module provaldi.self.towerdefensejavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;

    opens provaldi.self.tower.defense.javafx to javafx.fxml;
    opens provaldi.self.tower.defense.javafx.view.panes.gamefield to javafx.fxml;
    opens provaldi.self.tower.defense.javafx.view.panes.rightpanel to javafx.fxml;

    exports provaldi.self.tower.defense.javafx;
    exports provaldi.self.tower.defense.javafx.example;
    exports provaldi.self.tower.defense.javafx.logic;
    exports provaldi.self.tower.defense.javafx.view.panes.gamefield;
    exports provaldi.self.tower.defense.javafx.view.panes.rightpanel;
    exports provaldi.self.tower.defense.javafx.view.panes.upperpanel;
    exports provaldi.self.tower.defense.javafx.view.panes;
    exports provaldi.self.tower.defense.javafx.view.model;
    exports provaldi.self.tower.defense.javafx.view.model.interfaces;
    exports provaldi.self.tower.defense.javafx.view.model.entities;
    opens provaldi.self.tower.defense.javafx.view.panes to javafx.fxml;
}