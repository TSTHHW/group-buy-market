package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.tag.adapter.repository.ITagRepository;
import cn.bugstack.domain.tag.model.entity.CrowdTagsJobEntity;
import cn.bugstack.infrastructure.dao.ICrowdTagsDao;
import cn.bugstack.infrastructure.dao.ICrowdTagsDetailDao;
import cn.bugstack.infrastructure.dao.ICrowdTagsJobDao;
import cn.bugstack.infrastructure.dao.po.CrowdTags;
import cn.bugstack.infrastructure.dao.po.CrowdTagsDetail;
import cn.bugstack.infrastructure.dao.po.CrowdTagsJob;
import cn.bugstack.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Slf4j
@Repository
public class TagRepository implements ITagRepository {

    @Resource
    private ICrowdTagsDao crowdTagsDao;
    @Resource
    private ICrowdTagsDetailDao crowdTagsDetailDao;
    @Resource
    private ICrowdTagsJobDao crowdTagsJobDao;

    @Resource
    private IRedisService redisService;

    //获取采集人群的配置要求
    @Override
    public CrowdTagsJobEntity queryCrowdTagsJobEntity(String tagId, String batchId) {
        CrowdTagsJob crowdTagsJobReq = new CrowdTagsJob();
        crowdTagsJobReq.setTagId(tagId);
        crowdTagsJobReq.setBatchId(batchId);

        CrowdTagsJob crowdTagsJobRes = crowdTagsJobDao.queryCrowdTagsJob(crowdTagsJobReq);
        if (null == crowdTagsJobRes) return null;

        return CrowdTagsJobEntity.builder()
                .tagType(crowdTagsJobRes.getTagType())
                .tagRule(crowdTagsJobRes.getTagRule())
                .statStartTime(crowdTagsJobRes.getStatStartTime())
                .statEndTime(crowdTagsJobRes.getStatEndTime())
                .build();
    }

    //当系统确定某一个用户（userId）符合某一个标签（tagId）时，调用此方法。
    @Override
    public void addCrowdTagsUserId(String tagId, String userId) {
        log.info("开始添加用户标签 tagId:{} userId:{}", tagId, userId);

        CrowdTagsDetail crowdTagsDetailReq = new CrowdTagsDetail();
        crowdTagsDetailReq.setTagId(tagId);
        crowdTagsDetailReq.setUserId(userId);

        try {
            crowdTagsDetailDao.addCrowdTagsUserId(crowdTagsDetailReq);
            log.info("数据库写入成功 tagId:{} userId:{}", tagId, userId);

            // 获取BitSet
            RBitSet bitSet = redisService.getBitSet(tagId);
            int index = redisService.getIndexFromUserId(userId);
            log.info("准备写入Redis BitSet index:{} userId:{}", index, userId);
            bitSet.set(index, true);
            log.info("Redis BitSet写入成功 tagId:{} userId:{} index:{}", tagId, userId, index);
        } catch (DuplicateKeyException ignore) {
            // 忽略唯一索引冲突
            log.warn("用户标签已存在，跳过 tagId:{} userId:{}", tagId, userId);
        } catch (Exception e) {
            log.error("添加用户标签失败 tagId:{} userId:{}", tagId, userId, e);
            throw e;
        }
    }

    //统计对应标签的人群数量
    @Override
    public void updateCrowdTagsStatistics(String tagId, int count) {
        CrowdTags crowdTagsReq = new CrowdTags();
        crowdTagsReq.setTagId(tagId);
        crowdTagsReq.setStatistics(count);

        crowdTagsDao.updateCrowdTagsStatistics(crowdTagsReq);
    }

}
