package beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import model.Consulta;
import model.TipoAnimal;
import model.Veterinario;


@Named(value = "consultaBean")
@SessionScoped
public class ConsultaBean implements Serializable {

    
   @PersistenceContext
    EntityManager em;

    @Resource
    UserTransaction ctx;
    
    private Consulta consulta;
    private static LinkedList<SelectItem> vetsPorTipo;
    // para filtar os veterinários quando o usuário alterar o tipo de animal a consultar
    private TipoAnimal tipoAnimal;
    
    public ConsultaBean() {
        consulta = new Consulta();
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
    
    public String salvar() {
       try {
            ctx.begin();
            em.persist(consulta);
            ctx.commit();
            consulta = new Consulta();
        } catch (Throwable t) {
            t.printStackTrace();
            try { ctx.rollback(); } catch(Throwable t2) { }
        }
        return null;
    }

    public TipoAnimal getTipoAnimal() {
        return tipoAnimal;
    }

    public void setTipoAnimal(TipoAnimal tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
    }

    public List<SelectItem> getVetsPorTipo() {
        List<Veterinario> vets = em.createQuery("select v from "
                + " Veterinario v where v.especialidade = :x")
                .setParameter("x",tipoAnimal)
                .getResultList();
        vetsPorTipo = new LinkedList<>();
        vetsPorTipo.add(new SelectItem(null, "Selecion um Animal"));
        for (Veterinario v : vets) {
            vetsPorTipo.add(new SelectItem(v, v.getNome()));
        }
        return vetsPorTipo;
    }    
}
