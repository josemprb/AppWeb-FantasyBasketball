package webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import webapp.Entities.League;
import webapp.Entities.Lineup;
import webapp.Entities.Player;
import webapp.Entities.Team;
import webapp.Entities.User;
import webapp.Entities.User.Role;

@WebServlet("/SellPlayer")
public class SellPlayer extends HttpServlet {



	/**
	 * 
	 */
	private static final long serialVersionUID = -3503473401494936601L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		Configuration configuration = new Configuration();
		configuration.configure(this.getClass().getResource("/hibernate.cfg.xml"));
		configuration.addAnnotatedClass(Team.class);
		configuration.addAnnotatedClass(Player.class);
		configuration.addAnnotatedClass(Lineup.class);
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		Session ses = sessionFactory.openSession();
		Transaction tx = ses.beginTransaction();
		
		User user = (User) session.getAttribute("user");
		League league = (League) session.getAttribute("league");
		Lineup lineup = (Lineup) session.getAttribute("lineupUser");
		int id = Integer.parseInt(request.getParameter("id"));
		
		if (user.getRole() != Role.jugador) {
			System.out.println("Usuario o contraseña incorrectas");
			response.sendRedirect("login.jsp");
		} else if (lineup.getLeague() != league.getId()) {
			System.out.println("Usuario no inscrito en dicha liga.");
			response.sendRedirect("error.jsp");
		}
		
		String q1 = "from plantilladeportista where lineup = :lineup and player = :player";
		Query query1 = ses.createQuery(q1);
		query1.setParameter("player", id);
		query1.setParameter("lineup", lineup.getId());
		Team team = (Team) query1.list().get(0);
		
		String q2 = "from deportista where id = :player";
		Query query2 = ses.createQuery(q2);
		query2.setParameter("player", id);
		Player player = (Player) query2.list().get(0);
		long balance = lineup.getBalance() + (long) (player.getValue());
		lineup.setBalance(balance);
		
		List<Player> teamLineup = lineup.getTeamLineup();
		teamLineup.remove(player);
		lineup.setTeamLineup(teamLineup);
		
		ses.saveOrUpdate(lineup);
		ses.delete("from plantilladeportista where lineup = "+lineup.getId()+" and player = "+id, team);
		
		tx.commit();
		ses.close();
		
		RequestDispatcher rd = request.getRequestDispatcher("MarketHome.jsp");
		rd.forward(request, response);
	}
}
