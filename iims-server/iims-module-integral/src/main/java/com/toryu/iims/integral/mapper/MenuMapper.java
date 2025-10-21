package com.toryu.iims.integral.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.integral.model.dto.menu.MenuPageQueryDTO;
import com.toryu.iims.common.model.entity.integral.Menu;
import com.toryu.iims.integral.model.vo.admin.AdminMenuVO;
import com.toryu.iims.integral.model.vo.menu.MenuVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper {
    /**
     * 新增标签
     * @param menu Menu
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Menu menu);

    /**
     * 编辑标签
     * @param menu Menu
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Menu menu);

    /**
     * 标签分页查询
     * @param menuPageQueryDTO MenuPageQueryDTO
     * @return Page<MenuVO>
     */
    Page<MenuVO> pageQuery(MenuPageQueryDTO menuPageQueryDTO);

    /**
     * 根据id删除菜单
     * @param id 菜单ID
     */
    @Delete("delete from iims_integral_menu where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id查询标签
     * @param id 菜单ID
     * @return Menu
     */
    @Select("select * from iims_integral_menu where id = #{id}")
    Menu getById(Long id);

    /**
     * 查询所有菜单
     * @return List<MenuVO>
     */
    List<MenuVO> all();

    /**
     * 查询所有菜单
     * @return List<AdminMenuVo>
     */
    @Select("SELECT * FROM iims_integral_menu WHERE `status` = 1 order by parent_id, order_num;")
    List<AdminMenuVO> allMenu();
}
