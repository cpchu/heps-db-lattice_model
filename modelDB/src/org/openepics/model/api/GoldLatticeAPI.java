/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openepics.model.api;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.openepics.model.entity.GoldLattice;
import org.openepics.model.entity.Lattice;

/**
 *
 * @author chu
 */
public class GoldLatticeAPI {
    @PersistenceUnit
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("modelAPIPU");
    static EntityManager em = emf.createEntityManager();

    @PersistenceContext
    
    /**
     * get the gold model for the specified machine mode and model line
     * @param mode machine mode
     * @param line model line
     * @return the gold model for the specified machine mode and model line
     */
    public static GoldLattice getGoldLatticeForMachineModeAndModelLine(String mode, String line) {
        Query q;
        q = em.createQuery("SELECT g FROM Gold g WHERE "
                + "g.modelLineId.modelLineName = :lineName "
                + "AND g.machineModeId.machineModeName = :modeName "
                + "AND g.goldStatusInd = :gind").setParameter("lineName", line)
                .setParameter("modeName", mode).setParameter("gind", GoldLattice.PRESENT); 
        List<GoldLattice> gmList = q.getResultList();
        if (gmList.isEmpty()) {
            return null;
        }
        else { 
            return gmList.get(0);   
        }
    }
        
    /**
     * Tag a Lattice as Gold
     * 
     * @param l the Lattice to be tagged as Gold
     */
    public void setGoldLattice(Lattice l) {
        GoldLattice gl = new GoldLattice();
        Date date = new Date();
        gl.setCreateDate(date);
        gl.setCreatedBy(System.getProperty("user.name"));
        gl.setGoldStatusInd(GoldLattice.PRESENT);
        gl.setLatticeId(l);
        em.getTransaction().begin();
        em.persist(gl);
        
        // move present Gold to previous Gold
        try {
            GoldLattice g_old = getGoldLatticeForMachineModeAndModelLine(
                    gl.getLatticeId().getMachineModeId().getMachineModeName(), 
                gl.getLatticeId().getModelLineId().getModelLineName());
        
            g_old.setGoldStatusInd(GoldLattice.PREVIOUS);
            g_old.setUpdateDate(date);
            g_old.setUpdatedBy(System.getProperty("user.name"));
            em.persist(g_old);
        } 
        // if there is no Gold for this line/mode, skip it
        catch (NullPointerException e) {
            // do nothing
        }
        
        em.getTransaction().commit();
    }   
    
}
