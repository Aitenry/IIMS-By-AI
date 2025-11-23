package com.toryu.iims.integral.service.impl;

import com.toryu.iims.common.model.entity.integral.Organization;
import com.toryu.iims.integral.mapper.OrganizationMapper;
import com.toryu.iims.integral.model.vo.organization.OrganizationMenuVO;
import com.toryu.iims.integral.model.vo.organization.OrganizationVO;
import com.toryu.iims.integral.service.OrganizationService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 23:44
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    @Override
    public List<OrganizationMenuVO> getTreeMenus(Long companyId) {
        List<OrganizationMenuVO> allOrganizationMenus;
        if (Objects.isNull(companyId)) {
            // 如果companyId为null，可能表示查询所有顶级组织（parentId为0的）
            allOrganizationMenus = organizationMapper.getCompanyList(); // 请确保这个方法的SQL逻辑是查询parentId=0的
        } else {
            // 查询指定公司的组织架构，注意SQL已修改，只查询该公司的部门和职位
            allOrganizationMenus = organizationMapper.getCompanyOrgStructure(companyId);
        }

        if (allOrganizationMenus == null || allOrganizationMenus.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, OrganizationMenuVO> menuMap = new HashMap<>(); // 用于快速查找菜单项

        // 将所有菜单放入Map中，便于后续快速定位
        for (OrganizationMenuVO menu : allOrganizationMenus) {
            menuMap.put(menu.getId(), menu);
        }

        // 从所有菜单中筛选出根节点
        // 根节点的parentId应该等于传入的companyId
        // 如果companyId为null，则查找parentId为0的节点作为根
        List<OrganizationMenuVO> finalList = new ArrayList<>();
        Long rootParentId = (companyId != null) ? menuMap.get(companyId).getParentId() : 0L;
        for (OrganizationMenuVO menu : allOrganizationMenus) {
            if (Objects.equals(menu.getParentId(), rootParentId)) {
                finalList.add(menu);
                // 递归构建子菜单
                findChild(menu, menuMap);
            }
        }
        return finalList;
    }

    @Override
    public List<OrganizationVO> getAllMenuList() {
        List<OrganizationMenuVO> allOrganizationMenus = organizationMapper.getAllMenuList();

        if (allOrganizationMenus == null || allOrganizationMenus.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, OrganizationVO> menuMap = new HashMap<>(); // 用于快速查找菜单项

        // 将所有菜单放入Map中，便于后续快速定位，并转换为OrganizationVO
        for (OrganizationMenuVO menu : allOrganizationMenus) {
            menuMap.put(menu.getId(), OrganizationVO.builder()
                    .value(menu.getId()).label(menu.getName())
                    .type(menu.getType()).build());
        }

        List<OrganizationVO> finalList = new ArrayList<>();
        for (OrganizationMenuVO menu : allOrganizationMenus) {
            if (Objects.equals(menu.getParentId(), 0L)) {
                OrganizationVO vo = menuMap.get(menu.getId());
                finalList.add(vo);
                // 递归构建子菜单
                findChild(vo, allOrganizationMenus, menuMap);
            }
        }
        return finalList;
    }

    @Override
    public Organization getDepartmentByJobId(Long id) {
        return organizationMapper.getDepartmentByJobId(id);
    }

    @Override
    public List<Organization> getOrganizationByIds(List<Long> ids) {
        return organizationMapper.getOrganizationByIds(ids);
    }

    @Override
    public List<Organization> getDepartmentsByJobIds(ArrayList<Long> ids) {
        return organizationMapper.getDepartmentByJobIds(ids);
    }

    /**
     * 递归查找子菜单
     * @param menu 当前菜单项
     * @param allOriginalMenus 所有原始菜单列表
     * @param menuMap 所有菜单的Map映射，用于快速定位
     */
    private void findChild(OrganizationVO menu, List<OrganizationMenuVO> allOriginalMenus, Map<Long, OrganizationVO> menuMap) {
        List<OrganizationVO> children = new ArrayList<>();
        for (OrganizationMenuVO originalMenu : allOriginalMenus) {
            if (Objects.equals(originalMenu.getParentId(), menu.getValue())) {
                OrganizationVO childVo = menuMap.get(originalMenu.getId());
                if (childVo != null) {
                    children.add(childVo);
                    // 继续查找子菜单的子菜单
                    findChild(childVo, allOriginalMenus, menuMap);
                }
            }
        }
        menu.setChildren(children);
    }

    /**
     * 递归查找子菜单
     * @param menu 当前菜单项
     * @param menuMap 所有菜单的Map映射，用于快速定位
     */
    private void findChild(OrganizationMenuVO menu, Map<Long, OrganizationMenuVO> menuMap) {
        List<OrganizationMenuVO> children = new ArrayList<>();
        for (OrganizationMenuVO child : menuMap.values()) {
            if (Objects.equals(child.getParentId(), menu.getId())) { // 使用Objects.equals更安全
                children.add(child);
                // 继续查找子菜单的子菜单
                findChild(child, menuMap);
            }
        }
        menu.setChildren(children);
    }

}
