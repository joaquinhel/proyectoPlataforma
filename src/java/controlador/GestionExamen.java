package controlador;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.DAO.InterfazExamen;
import modelo.DAO.InterfazImparticion;
import modelo.DAO.InterfazMatricula;
import persistencia.Examen;
import persistencia.Imparticion;
import persistencia.Matricula;
import persistencia.Pregunta;

@ManagedBean
@SessionScoped
public class GestionExamen {

    @ManagedProperty("#{eDAO}")
    private InterfazExamen eDAO;
    @ManagedProperty("#{mDAO}")
    private InterfazMatricula mDAO;
    @ManagedProperty("#{iDAO}")
    private InterfazImparticion iDAO;
    private List<Examen> preguntas;
    private List<PreguntaExamen> preguntasExamen;
    private int idImparticion;
    private String urlTemario;
    private double nota;
    private String dni;
    private boolean mostrarBoton;
    private Imparticion imparticion;

    public GestionExamen() {
    }

    /**
     * Método que se encargará de la corrección de los exámenes. Controlará que
     * una vez puesta la nota no se pueda enviar de nuevo.
     *
     * @return
     */
    public String corregir() {
        nota = 0;
        int incrementoNota;
        incrementoNota = 10 / preguntas.size();
        //PreguntaExamen es una clase del controlador, contiene un collection de
        //respuestas, un objeto de pregunta y un int de la respuesta(valueRespuesta)
        //PreguntasExamen es la lista que hemos declarado como atributo en esta clase.
        for (PreguntaExamen pe : preguntasExamen) {
            //Comparamos la respuesta introducida con la correcta
            if (pe.getValueRespuesta() == pe.getPregunta().getRespuestaCorrecta()) {
                //Si existe coincidencia sumamos dos puntos
                nota += incrementoNota;
            }
        }
        System.out.println(nota);
        mDAO.ponerNota(dni, idImparticion, nota);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Examen realizado", "Su nota es: " + nota));
        mostrarBoton = false;
        return null;
    }

    /**
     * Método que se encargará de cargar el exámen en caso de que no se haya
     * realizado. Si ya ha sido realizado mostrará un aviso indicando que ya ha
     * sido realizado junto con la nota. Cada vez que pulsemos en enviar el
     * examen se recargará examen.xhtml
     *
     * @param dni
     * @param idImparticion
     * @return examen
     */
    public String cargar(String dni, int idImparticion) {
        this.dni = dni;
        this.idImparticion = idImparticion;
        mostrarBoton = true;
        Matricula matricula = mDAO.buscarMatricula(dni, idImparticion);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = null;
        imparticion = iDAO.buscarImparticionID(idImparticion);
        try {
            fechaActual = sd.parse(sd.format(new Date()));
        } catch (ParseException ex) {
            ex.getMessage();
        }
        if (matricula.getNota() != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Examen ya realizado", "Su nota es: " + matricula.getNota()));
            return null;
            //En caso de que nota (nota=null)
        } else {
            preguntasExamen = new ArrayList<>();
            preguntas = eDAO.cargarExamen(idImparticion);
            if (preguntas.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Examen no disponible", "No existe examen disponible del curso: " + matricula.getIdImparticion().getNombre()));
                return null;
            } else if (imparticion.getFechaFin().compareTo(fechaActual) < 0) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Examen no disponible", "El curso " + imparticion.getNombre() + " ha finalizado."));
                return null;
            } else {
                for (Examen p : preguntas) {
                    PreguntaExamen pe = new PreguntaExamen();
                    Collection respuestas = p.getIdPregunta().getRespuestaCollection();
                    Pregunta pregunta = p.getIdPregunta();
                    pe.setPregunta(pregunta);
                    pe.setRespuestas(respuestas);
                    preguntasExamen.add(pe);
                }
                return "examen";
            }
        }
    }

    /**
     * Método que se encargará de recuperar el temario en .pdf y mostrarlo en
     * "temario.xhtml"
     *
     * @param idImparticion
     * @return temario
     */
    public String temario(int idImparticion) {
        this.idImparticion = idImparticion;
        Imparticion i = iDAO.buscarTemario(idImparticion);
        String nombre = i.getIdCurso().getDocumento();
        urlTemario = "resources/" + nombre;
        return "temario";
    }

    
    
    public List<Examen> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Examen> preguntas) {
        this.preguntas = preguntas;
    }

    public int getIdImparticion() {
        return idImparticion;
    }

    public void setIdImparticion(int idImparticion) {
        this.idImparticion = idImparticion;
    }

    public String getUrlTemario() {
        return urlTemario;
    }

    public void setUrlTemario(String urlTemario) {
        this.urlTemario = urlTemario;
    }

    public List<PreguntaExamen> getPreguntasExamen() {
        return preguntasExamen;
    }

    public void setPreguntasExamen(List<PreguntaExamen> preguntasExamen) {
        this.preguntasExamen = preguntasExamen;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public boolean isMostrarBoton() {
        return mostrarBoton;
    }

    public void setMostrarBoton(boolean mostrarBoton) {
        this.mostrarBoton = mostrarBoton;
    }

    public Imparticion getImparticion() {
        return imparticion;
    }

    public void setImparticion(Imparticion imparticion) {
        this.imparticion = imparticion;
    }

    public InterfazExamen geteDAO() {
        return eDAO;
    }

    public void seteDAO(InterfazExamen eDAO) {
        this.eDAO = eDAO;
    }

    public InterfazMatricula getmDAO() {
        return mDAO;
    }

    public void setmDAO(InterfazMatricula mDAO) {
        this.mDAO = mDAO;
    }

    public InterfazImparticion getiDAO() {
        return iDAO;
    }

    public void setiDAO(InterfazImparticion iDAO) {
        this.iDAO = iDAO;
    }

}
