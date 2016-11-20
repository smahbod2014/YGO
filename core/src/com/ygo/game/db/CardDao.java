package com.ygo.game.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(CardDefinitionMapper.class)
public interface CardDao {

    @SqlQuery("select * from cards where serial=:serial")
    CardDefinition getCardBySerial(@Bind("serial") String serial);

    @SqlQuery("select * from cards where name=:name collate nocase")
    CardDefinition getCardByName(@Bind("name") String name);
}
