package Sistema;

import Usuarios.*;
import repositorios.RepositorioOfertas;

public abstract class SubSistema {
    protected Usuario usuario;
    public SubSistema(Usuario usuario) {
        this.usuario = usuario;
    }

    public abstract void mostrarMenu();
    public abstract void salir();
    protected RepositorioOfertas repoOfertas = new RepositorioOfertas();

    
}