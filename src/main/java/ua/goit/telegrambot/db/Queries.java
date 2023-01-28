package ua.goit.telegrambot.db;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ua.goit.telegrambot.api.dto.BankNAME;
import ua.goit.telegrambot.api.dto.Currency;
import ua.goit.telegrambot.settings.User;

import java.util.List;

public class Queries {

    public Long createUser(User user){

        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        session.close();
        return user.getId();
    }

    public User getUser(long userId){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            System.out.println("user = " + user);
        tx.commit();
        session.close();
        return user;
    }

    public  <T> List<T> loadAllData(Class<T> type) {
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(type);
        criteria.from(type);
        List<T> data = session.createQuery(criteria).getResultList();

        tx.commit();
        session.close();
        return data;
    }

    public void setEnglish(long userId){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User user = getUser(userId);
        user.setEnglish(true);
        user.setUkrainian(false);
        session.saveOrUpdate(user);
        tx.commit();
        session.close();
    }

    public void setUkrainian(long userId){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User user = getUser(userId);
        user.setEnglish(false);
        user.setUkrainian(true);
        session.saveOrUpdate(user);
        tx.commit();
        session.close();
    }

    public BankNAME setBank(long userId, BankNAME bank){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            User user = getUser(userId);
            user.setBank(bank);
            session.saveOrUpdate(user);
        tx.commit();
        session.close();
        return user.getBank();
    }

    public int setRounding(long userId, int rounding){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User user = getUser(userId);
        user.setRounding(rounding);
        session.saveOrUpdate(user);
        tx.commit();
        session.close();
        return user.getRounding();
    }

    public int setCurrency(long userId, Currency currency){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User user = getUser(userId);
        user.setCurrency(currency);
        session.saveOrUpdate(user);
        tx.commit();
        session.close();
        return user.getRounding();
    }

    public boolean setScheduler(long userId, boolean scheduler){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User user = getUser(userId);
        user.setScheduler(scheduler);
        session.saveOrUpdate(user);
        tx.commit();
        session.close();
        return user.isScheduler();
    }

    public int setSchedulerTime(long userId, int schedulerTime){
        Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User user = getUser(userId);
        user.setSchedulerTime(schedulerTime);
        session.saveOrUpdate(user);
        tx.commit();
        session.close();
        return user.getSchedulerTime();
    }
}
