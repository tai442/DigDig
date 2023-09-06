package plugin.digdig.mapper;

import org.apache.ibatis.annotations.Select;
import plugin.digdig.mapper.data.GameTime;

public interface GameTimeMapper {
  @Select("select * from game_config")
  GameTime getGameTime();
}
