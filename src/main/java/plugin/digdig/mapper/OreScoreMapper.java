package plugin.digdig.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Select;
import plugin.digdig.mapper.data.OreScore;

public interface OreScoreMapper {
  @Select("select ore_type, score from spawn_ore")
  List<OreScore> selectList();
}
