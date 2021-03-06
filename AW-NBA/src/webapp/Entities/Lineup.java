package webapp.Entities;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity(name="plantilla")
@Table
public class Lineup implements Serializable {
	@Id
	@Column(name = "id")
	private int id;
	//@ManyToOne(targetEntity = League.class)
	@Column(name="liga")
	private int league;
	//@ManyToOne(targetEntity = User.class)
	@Column(name="usuario")
	private int user;
	@Column(name = "saldo")
	private long balance;	//125000 max; Salario de la plantilla de una liga determinada
						//no del usuario en general
	@Transient
	private List<Player> teamLineup;
	@Column(name = "puntos")
	private int points;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLeague() {
		return league;
	}
	public void setLeague(int league) {
		this.league = league;
	}
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public List<Player> getTeamLineup() {
		return teamLineup;
	}
	public void setTeamLineup(List<Player> teamLineup) {
		this.teamLineup = teamLineup;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	
	@Override
	public String toString() {
		return "Lineup [id=" + id + ", league=" + league + ", user=" + user + ", balance=" + balance + ", teamLineup="
				+ teamLineup + ", points=" + points + "]";
	}

	
}
