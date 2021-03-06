package webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

/**
 * Servlet implementation class BuyPlayer
 */
@WebServlet("/BuyPlayer")
public class BuyPlayer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		Configuration configuration = new Configuration();
		configuration.configure(this.getClass().getResource("/hibernate.cfg.xml"));
		configuration.addAnnotatedClass(Player.class);
		configuration.addAnnotatedClass(Team.class);
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
			System.out.println("Error, redireccionar a p�gina de error.");
			response.sendRedirect("error.jsp");
		}
		
		String q1 = "from deportista where id = :id";
		Query query1 = ses.createQuery(q1);
		query1.setParameter("id", id);
		Player player = (Player) query1.list().get(0);
		
		
		String q2 = "from plantilladeportista";
		Query query2 = ses.createQuery(q2);
		List<Team> listTeams = (List<Team>) query2.list();
		
		long balance = lineup.getBalance();
		boolean canBuy = true;
		List<Player> teamLineup = lineup.getTeamLineup();
		
		if (teamLineup == null) {
			//Empty lineup: create new array of players and fill
			List<Player> teamPlayers = new ArrayList<Player>();
			teamPlayers.add(player);
			lineup.setTeamLineup(teamPlayers);
			lineup.setBalance(balance - (long) player.getValue());
			System.out.println("La plantilla est� vac�a.");
			System.out.println("Se puede comprar dicho jugador.");
			Team team = new Team();
			Random rn = new Random();
			team.setId(rn.nextInt());
			team.setPlayer(id);
			team.setLineup(lineup.getId());
			listTeams.add(team);
			ses.saveOrUpdate(team);
			ses.saveOrUpdate(lineup);
		} else if (balance < player.getValue()) {
			//no money
			canBuy = false;
			System.out.println("No hay suficiente saldo para comprar dicho jugador");
		} else if (teamLineup.size() >= 5) {
			//no space
			canBuy = false;
			System.out.println("El equipo ya cuenta con el n�mero m�ximo de deportistas");
		} else if (balance > player.getValue() && teamLineup.size() < 5) {
			//CAN BUY
				//loop checking you can't buy same player twice
			for (Player p : teamLineup) {
				if (p.getId() == player.getId()) {
					//Cannot buy player, you already have that player
					canBuy = false;
				}
			}
			if(canBuy) {
				teamLineup.add(player);
				lineup.setBalance(balance - (long) player.getValue());
				ses.saveOrUpdate(lineup);
				System.out.println("Se puede comprar dicho jugador.");
				Team team = new Team();
				Random rn = new Random();
				team.setId(rn.nextInt());
				team.setPlayer(id);
				team.setLineup(lineup.getId());
				listTeams.add(team);
				ses.saveOrUpdate(team);
			}
		} 
		
		tx.commit();
		ses.close();
		
		RequestDispatcher rd = request.getRequestDispatcher("MarketHome.jsp");
		rd.forward(request, response);
		
	}

}