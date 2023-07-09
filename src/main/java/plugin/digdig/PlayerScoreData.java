package plugin.digdig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.digdig.mapper.PlayerScoreMapper;
import plugin.digdig.mapper.data.PlayerScore;

/**
 * DB接続やそれに付随する登録や更新処理を行う
 */

public class PlayerScoreData {

  private final PlayerScoreMapper mapper;

  public PlayerScoreData() {
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession session = sqlSessionFactory.openSession(true);
      this.mapper = session.getMapper(PlayerScoreMapper.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * プレイヤースコアテーブルから一覧でスコア情報を取得する。
   *
   * @return スコア情報の一覧
   */
  public List<PlayerScore> selectList() {
      return mapper.selectList();
  }

  /**
   * プレイヤースコアテーブルにスコア情報を登録する。
   *
   * @param playerScore プレイヤースコア
   */
  public void insert(PlayerScore playerScore) {
      mapper.insert(playerScore);
  }
}
