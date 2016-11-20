package com.ygo.game.db;

import com.ygo.game.Types.Attribute;
import com.ygo.game.Types.CardType;
import com.ygo.game.Types.Race;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CardDefinitionMapper implements ResultSetMapper<CardDefinition> {
    @Override
    public CardDefinition map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new CardDefinition(
                r.getString("name"),
                r.getString("serial"),
                r.getInt("atk"),
                r.getInt("def"),
                r.getInt("level"),
                r.getString("attribute") == null ? null : Attribute.valueOf(r.getString("attribute")),
                r.getString("race") == null ? null : Race.valueOf(r.getString("race")),
                CardType.valueOf(r.getString("type")),
                r.getString("flavors"),
                r.getString("text") == null ? "" : r.getString("text")
        );
    }
}
