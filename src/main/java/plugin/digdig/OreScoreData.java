package plugin.digdig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.digdig.mapper.OreScoreMapper;
import plugin.digdig.mapper.data.OreScore;

public class OreScoreData {

  private final OreScoreMapper mapper;

  public OreScoreData() {
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession session = sqlSessionFactory.openSession(true);
      this.mapper = session.getMapper(OreScoreMapper.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * テーブルから一覧でスコア情報を取得する。
   *
   * @return 鉱石のスコア情報の一覧
   */
  public List<OreScore> getAllOreScores() {
    return mapper.selectList();
  }
}