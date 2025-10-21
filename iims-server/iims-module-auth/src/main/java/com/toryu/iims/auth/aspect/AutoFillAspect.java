package com.toryu.iims.auth.aspect;

import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.constant.AutoFillConstant;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.utils.SnowFlakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    private final SnowFlakeIdWorker adminSnowFlakeIdWorker;
    private final SnowFlakeIdWorker archiveSnowFlakeIdWorker;
    private final SnowFlakeIdWorker aiSnowFlakeIdWorker;
    private final SnowFlakeIdWorker commonSnowFlakeIdWorker;

    public AutoFillAspect(
            SnowFlakeIdWorker adminSnowFlakeIdWorker,
            SnowFlakeIdWorker archiveSnowFlakeIdWorker,
            SnowFlakeIdWorker aiSnowFlakeIdWorker,
            SnowFlakeIdWorker commonSnowFlakeIdWorker) {
        this.adminSnowFlakeIdWorker = adminSnowFlakeIdWorker;
        this.archiveSnowFlakeIdWorker = archiveSnowFlakeIdWorker;
        this.aiSnowFlakeIdWorker = aiSnowFlakeIdWorker;
        this.commonSnowFlakeIdWorker = commonSnowFlakeIdWorker;
    }

    @Pointcut("@annotation(com.toryu.iims.common.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("{} >> {} >> 进行公共字段自动填充...", signature.getDeclaringTypeName(), method.getName());

        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if (operationType == OperationType.INSERT) {
            fillInsertFields(joinPoint, entity, now, currentId);
        } else if (operationType == OperationType.UPDATE) {
            fillUpdateFields(entity, now, currentId);
        }
    }

    private void fillInsertFields(JoinPoint joinPoint, Object entity, LocalDateTime now, Long currentId) {
        if (entity instanceof List) {
            ((List<?>) entity).forEach(e -> fillInsertFields(joinPoint, e, now, currentId));
            return;
        }

        SnowFlakeIdWorker snowFlakeIdWorker = getSnowFlakeIdWorker(joinPoint);
        if (snowFlakeIdWorker == null) {
            log.warn("No suitable SnowFlakeIdWorker found.");
            return;
        }

        try {
            Method setCreateTime = entity.getClass()
                    .getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateBy = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_BY, Long.class);
            Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateBy = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_BY, Long.class);
            Method setId = entity.getClass().getMethod(AutoFillConstant.SET_ID, Long.class);

            setCreateTime.invoke(entity, now);
            setUpdateTime.invoke(entity, now);
            setId.invoke(entity, snowFlakeIdWorker.nextId());
            if (currentId != null) {
                setCreateBy.invoke(entity, currentId);
                setUpdateBy.invoke(entity, currentId);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("Failed to auto fill insert fields", e);
        }
    }

    private void fillUpdateFields(Object entity, LocalDateTime now, Long currentId) {
        try {
            Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateBy = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_BY, Long.class);

            setUpdateTime.invoke(entity, now);
            if (currentId != null) {
                setUpdateBy.invoke(entity, currentId);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("Failed to auto fill update fields", e);
        }
    }

    private SnowFlakeIdWorker getSnowFlakeIdWorker(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getDeclaringTypeName();
        if (methodName.startsWith("com.toryu.iims.integral.mapper")) {
            return adminSnowFlakeIdWorker;
        } else if (methodName.startsWith("com.toryu.dms.archive.mapper")) {
            return archiveSnowFlakeIdWorker;
        } else if (methodName.startsWith("com.toryu.iims.ai.chat.mapper")) {
            return aiSnowFlakeIdWorker;
        } else if (methodName.startsWith("com.toryu.iims.common.mapper")) {
            return commonSnowFlakeIdWorker;
        }
        return null;
    }
}