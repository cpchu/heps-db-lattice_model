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
import org.openepics.model.entity.Element;
import org.openepics.model.entity.ElementProp;
import org.openepics.model.entity.ElementType;

/**
 *
 * @author chu
 */
public class ElementAPI {
    @PersistenceUnit
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("modelAPIPU");
    static EntityManager em = emf.createEntityManager();

    @PersistenceContext
    
    /**
     * get all elements within the specified sequence
     * 
     * @param seq sequence name
     * @return elements within the specified sequence
     */
    public static List<Element> getAllElementForSequence(String seq) {
        Query q;
        q = em.createQuery("SELECT e FROM Element e JOIN e.sequenceId s "
                + "WHERE s.sequenceName = :sequenceName").setParameter("sequenceName", seq);
        List<Element> eList = q.getResultList();
        return eList;
    }
    
    /**
     * get all properties for the specified element
     * @param elementName element name
     * @return all properties for the specified element
     */
    public static List<ElementProp> getAllPropertiesForElement(String elementName) {
        Query q;
        q = em.createQuery("SELECT ep FROM ElementProp ep JOIN ep.elementId e "
                + "WHERE e.elementName = :elementName").setParameter("elementName", elementName);
        List<ElementProp> epList = q.getResultList();
        return epList;
    }
    
    /**
     * 
     * @param s
     * @param len
     * @param dx
     * @param dy
     * @param dz
     * @param pitch
     * @param yaw
     * @param roll
     * @param pos 
     */
    public void setElement(double s, double len, double dx, double dy, double dz, double pitch, double yaw, double roll, double pos) {
        // TODO save an individual element's model data
        Element e = new Element();
        Date date = new Date();
        e.setInsertDate(date);
        e.setCreatedBy(System.getProperty("user.name"));
        e.setDx(dx);
        e.setDy(dy);
        e.setDz(dz);
        e.setPitch(pitch);
        e.setYaw(yaw);
        e.setRoll(roll);
        e.setPos(pos);
//        e.setBeamParameterCollection(null);
        
    }
    
    /**
     * Set a new element type
     * @param elem_type element type
     * @param elem_type_desc description for this element type
     */
    public static void setElementType(String elem_type, String elem_type_desc) {
        ElementType et = new ElementType();
        et.setElementType(elem_type);
        et.setElementTypeDescription(elem_type_desc);
        em.getTransaction().begin();
        em.persist(et);
        em.getTransaction().commit();
    }
    
    
}
