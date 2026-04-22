package cn.bugstack.domain.tag.service;

import cn.bugstack.domain.tag.adapter.repository.ITagRepository;
import cn.bugstack.domain.tag.model.entity.CrowdTagsJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TagService implements ITagService{

    @Resource
    private ITagRepository tagRepository;

    @Override
    public void execTagBatchJob(String tagId, String batchId) {
        log.info("人群标签批次任务 tagId:{} batchId:{}", tagId, batchId);

        CrowdTagsJobEntity crowdTagsJobEntity = tagRepository.queryCrowdTagsJobEntity(tagId, batchId);
        log.info("查询到批次任务配置: {}", crowdTagsJobEntity);

        if (null == crowdTagsJobEntity) {
            log.warn("批次任务配置不存在，跳过执行");
            return;
        }

        //数据写入
        List<String> userIdList = new ArrayList<String>() {{
            add("xiaofuge");
            add("liergou");
            add("whh1");
            add("xhg2");
            add("wjj3");
            add("ooo4");
            add("tst5");
            add("uuu6");
            add("iii7");
            add("pjk8");
            add("pkm9");
        }};

        log.info("准备写入用户列表: {}", userIdList);
        for (String userId : userIdList) {
            tagRepository.addCrowdTagsUserId(tagId, userId);
        }

        tagRepository.updateCrowdTagsStatistics(tagId, userIdList.size());
    }
}
