/**
 * 
 */
/**
 * 
 */
module Proyecto_DPO {
    requires com.google.gson;
	requires org.junit.jupiter.api;

    opens repositorios to com.google.gson;
    opens Eventos to com.google.gson;
    opens Usuarios to com.google.gson;
    opens tiquetes to com.google.gson;
}
