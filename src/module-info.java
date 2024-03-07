module Galaga {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.media;
	opens application to javafx.fxml;
	exports application;
}
