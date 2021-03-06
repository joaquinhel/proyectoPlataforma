package controlador;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import modelo.DAO.InterfazCurso;
import persistencia.Curso;

@ManagedBean
@SessionScoped
/**
 * Clase que se encargará de recuperar una lista de cursos
 */
public class ListaCursos {

    @ManagedProperty("#{cuDAO}")
    private InterfazCurso cDAO;
    private List<Curso> lista;
    private String nombre;

    public ListaCursos() {
    }

    /**
     * Este método llama al método de la interfaz que se encargará de listar
     * todos los cursos de la base de datos y nos devulve a "imparticion.xhtml"
     *
     * @return imparticion
     */
    public String listarCursos() {
        lista = cDAO.listarCursos();
        return "imparticion";
    }

    /**
     * Este método llama al método de la interfaz que se encargará de listar
     * todos los cursos de la base de datos en cuyo nombre exista el string
     * pasado como parámetro y nos devuenve a "mostrarCursos.xhtml"
     *
     * @return mostrarCursos
     */
    public String buscador() {
        lista = cDAO.listarCursosNombre(nombre);
        return "mostrarCursos";
    }

    public List<Curso> getLista() {
        return lista;
    }

    public void setLista(List<Curso> lista) {
        this.lista = lista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public InterfazCurso getcDAO() {
        return cDAO;
    }

    public void setcDAO(InterfazCurso cDAO) {
        this.cDAO = cDAO;
    }

}
