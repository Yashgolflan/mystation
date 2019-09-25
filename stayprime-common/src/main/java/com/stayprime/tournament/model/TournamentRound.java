/*
 * 
 */
package com.stayprime.tournament.model;

import com.stayprime.tournament.util.TournamentUtil;
import com.stayprime.tournament.util.XmlDateTimeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author benjamin
 */
//@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"tournamentId", "number"}))
@XmlAccessorType(XmlAccessType.FIELD)
public class TournamentRound implements Comparable<TournamentRound> {

    @Id @GeneratedValue
    @XmlAttribute
    private int id;

    @XmlAttribute
    private String extId;

    @XmlAttribute
    private int number;

    // Details

    @XmlTransient
    @Transient
    private Tournament tournament;

    private String tournamentName;

    @XmlAttribute
    @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
    private Date date;

    private int courseId;

    private String extCourseId;

    // Players, assignments and groups

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "roundId") @OrderBy("name")
    @XmlElementWrapper
    @XmlElement(name = "player")
    private List<Player> playersList;

    @Transient
    @XmlTransient
    private List<Leaderboard> leaderboards;

    private Date updated;

    @Transient
    @XmlTransient
    // Transient: mapped separately by players and cartsForScoring getter/setter
    private final CartAssignments cartAssignments;

//    @Transient
//    @XmlTransient
//    private List<ScoringGroup> groups;

    // Excluded fields from reflection identity methods
    private static final String[] excludeFields = new String[] {
        "players", "cartAssignments", "groups", "tournament"
    };

    TournamentRound() {
        this(0);
    }

    public TournamentRound(int number) {
        this.number = number;
        cartAssignments = new CartAssignments();
        playersList = new ArrayList<Player>();
        leaderboards = new ArrayList<Leaderboard>();
    }

    public int getId() {
        return id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getExtCourseId() {
        return extCourseId;
    }

    public void setExtCourseId(String extCourseId) {
        this.extCourseId = extCourseId;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }

    public void setPlayersList(List<Player> players) {
        if (players != null) {
            this.playersList = players;
        }
        else {
            this.playersList = Collections.EMPTY_LIST;
        }
    }

    public List<Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    public void setLeaderboards(List<Leaderboard> leaderboards) {
        this.leaderboards = leaderboards;
    }

    public CartAssignments getCartAssignments() {
        return cartAssignments;
    }

    public void setCartAssignments(CartAssignments cartAssignments) {
        updateCartAssignments(cartAssignments);
    }

    private void updateCartAssignments(CartAssignments newAssignments) {
        for (Player p : playersList) {
            p.setCart(null);
        }

        cartAssignments.clear();

        if (newAssignments != null) {
            for (CartAssignment nca : newAssignments.getAssignmentsList()) {
                cartAssignments.setForScoring(nca.getCartNumber(), nca.isForScoring());
                int cn = nca.getCartNumber();
                for (Player externalPlayer : nca.getPlayers()) {
                    Player internalPlayer = findPlayer(externalPlayer);
                    cartAssignments.addCartPlayer(cn, internalPlayer);
                }
            }
        }
    }

    public void updateCartAssignmentsFromPlayers() {
        cartAssignments.loadFromPlayersList(playersList);
    }

    private Player findPlayer(Player externalPlayer) {
        for (Player p : playersList) {
            if (p.isSamePlayer(externalPlayer)) {
                return p;
            }
        }
        return null;
    }

    @Access(AccessType.PROPERTY)
    @XmlElement
    public String getCartsForScoring() {
        return cartAssignments.getCartsForScoring();
    }

    public void setCartsForScoring(String s) {
        cartAssignments.setCartsForScoring(s);
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /*
     * Copy properties methods
     */

    public void copyDetails(TournamentRound r) {
        setDate(r.getDate());
        setExtCourseId(r.getExtCourseId());
    }

    public void copyPlayers(TournamentRound oldR) {
        List<Player> newPlayers = new ArrayList<Player>(oldR.getPlayersList().size());
        for (Player oldP : oldR.getPlayersList()) {
            Player newP = new Player(oldP.getExtId(), oldP.getName());
            newPlayers.add(newP);
        }
        setPlayersList(newPlayers);
    }

    public void setDbInfo(TournamentRound dbRound) {
        this.id = dbRound.getId();
        if (ObjectUtils.equals(extCourseId, dbRound.getExtCourseId())) {
            this.courseId = dbRound.getCourseId();
        }
        setPlayersDbInfo(dbRound);
    }

    private void setPlayersDbInfo(TournamentRound dbRound) {
        List<Player> dbPlayers = dbRound.getPlayersList();
        if (dbPlayers != null) {
            for (Player dbPlayer : dbPlayers) {
                String dbExtId = dbPlayer.getExtId();
                int i = TournamentUtil.findPlayerByExtId(playersList, dbExtId);
                if (i >= 0) {
                    Player roundPlayer = playersList.get(i);
                    roundPlayer.setId(dbPlayer.getId());
                }
            }
        }
    }

    public List<Player> getPlayersForCart(int cartNumber) {
        return getPlayersForCart(playersList, cartNumber);
    }

    public static List<Player> getPlayersForCart(List<Player> list, int cartNumber) {
        if (cartNumber == 0) {
            return Collections.EMPTY_LIST;
        }

        List<Player> players = new ArrayList<Player>();
        for (Player p : list) {
            if (p.getCart() == cartNumber) {
                players.add(p);
            }
        }
        return players;
    }

    /*
     * Comparison and override methods
     */

    @Override
    public int compareTo(TournamentRound r) {
        return Integer.compare(number, r.getNumber());
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, excludeFields);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, excludeFields);
    }

    @Override
    public String toString() {
        return (tournamentName == null? "Round " : tournamentName + " R") + number;
    }

    public static boolean isSameRound(TournamentRound r1, TournamentRound r2) {
        if (r1 != null && r2 != null) {
            boolean sameId = StringUtils.equals(r1.getExtId(), r2.getExtId());
            boolean sameNumber = r1.getNumber() == r2.getNumber();
            return sameId && sameNumber;
        }
        else {
            return r1 == r2;
        }
    }

}
