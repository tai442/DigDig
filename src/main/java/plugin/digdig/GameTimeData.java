package plugin.digdig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.digdig.mapper.GameTimeMapper;
import plugin.digdig.mapper.OreScoreMapper;
import plugin.digdig.mapper.data.GameTime;
import plugin.digdig.mapper.data.OreScore;

public class GameTimeData {

  private final GameTimeMapper mapper;

  public GameTimeData() {
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession session = sqlSessionFactory.openSession(true);
      this.mapper = session.getMapper(GameTimeMapper.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  /**
   * テーブルからゲーム時間を取得する。
   *
   * @return ゲーム時間
   */
  public int getGameTime() {
    GameTime gameTime = mapper.getGameTime();
    return gameTime.getGameTime();
  }
}