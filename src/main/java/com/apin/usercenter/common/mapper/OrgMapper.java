package com.apin.usercenter.common.mapper;

import com.apin.usercenter.common.entity.Organization;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 郑昊
 * @date 2017/10/30
 * @remark 部门相关DAL
 */
@Mapper
public interface OrgMapper extends Mapper {

    /**
     * 获取全部组织机构节点
     *
     * @return 组织机构集合
     */
    @Select("select * from organization")
    List<Organization> getOrgs();

    /**
     * 增加职位成员
     *
     * @param postId 职位ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    @Insert("insert into post_member values (REPLACE(uuid(),'-',''),#{postId},#{userId})")
    Integer addPostMember(@Param("postId") String postId, @Param("userId") String userId);

    /**
     * 移除职位成员
     *
     * @param postId 职位ID
     * @param userId 用户ID
     * @return 受影响行数
     */
    @Insert("delete from post_member where post_id=#{postId} and user_id =#{userId}")
    Integer removePostMember(@Param("postId") String postId, @Param("userId") String userId);

    /**
     * 获取用户所属部门
     *
     * @param userId 用户ID
     * @return 组织机构集合
     */
    @Select("SELECT o.* FROM organization o JOIN post_member m ON m.post_id=o.id AND m.user_id=#{userId} UNION " +
            "SELECT o.* FROM organization o JOIN organization d ON d.parent_id=o.id JOIN post_member m ON m.post_id=d.id AND m.user_id=#{userId};")
    List<Organization> getOrg(String userId);
}
