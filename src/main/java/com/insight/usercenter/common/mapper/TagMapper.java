package com.insight.usercenter.common.mapper;

import com.insight.usercenter.common.entity.Device;
import com.insight.usercenter.common.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author luwenbao
 * @date 2018/1/4.
 * @remark 用户标签相关DAL
 */
@Mapper
public interface TagMapper extends Mapper {

    /**
     * 根据条件获取在线(或离线)设备id
     *
     * @param userIdList 用户id集合
     * @param tagList    标签集合
     * @param type       查询类型 1全部  2 在线
     * @return 设备id集合
     */
    @Select("<script>SELECT DISTINCT a.id FROM ucb_user_device a INNER JOIN ucb_user_tag b ON a.`user_id` = b.`user_id` WHERE 1=1 " +
            "<if test='type==2' > AND a.`is_invalid` = 0 </if>" +

            "<if test='null != userIdList and null != tagList' >" +
            " AND (a.user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach>" +
            " OR b.tag in " +
            "<foreach collection = \"tagList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach>) </if>" +

            "<if test='null == userIdList and null != tagList' >" +
            " AND b.tag in " +
            "<foreach collection = \"tagList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach> </if>" +

            "<if test='null != userIdList and null == tagList' >" +
            " AND a.user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach> </if>" +
            "</script>")
    List<String> getDevices(@Param("userIdList") List<String> userIdList, @Param("tagList") List<String> tagList, @Param("type") int type);

    /**
     * 获取指定ID的标签
     *
     * @param id 标签ID
     * @return 标签实体
     */
    @Select("SELECT * FROM ucb_user_tag WHERE id = #{id}")
    Tag getTag(String id);

    /**
     * 根据用户id和标签tag查询tag是否唯一
     *
     * @param userId
     * @param tag
     * @return
     */
    @Select("SELECT * FROM ucb_user_tag WHERE user_id = #{userId} and tag = #{tag}")
    Tag getTagByUserIdAndTag(@Param("userId") String userId, @Param("tag") String tag);

    /**
     * 新增用户标签
     *
     * @param tag
     * @return
     */
    @Insert("INSERT ucb_user_tag (id,user_id,tag,creator_user_id,created_time) " +
            "VALUES (#{id},#{userId},#{tag},#{creatorUserId},#{createdTime})")
    Integer addTag(Tag tag);

    /**
     * 删除用户标签
     *
     * @param id 标签id
     * @return 受影响行数
     */
    @Delete("DELETE FROM ucb_user_tag WHERE id = #{id} ")
    Integer deleteTag(String id);

    /**
     * 更新用户标签数据
     *
     * @param tag 标签实体
     * @return 受影响行数
     */
    @Update("UPDATE ucb_user_tag SET `tag`=#{tag},created_time=#{createdTime} WHERE id=#{id}")
    Integer updateTag(Tag tag);

    /**
     * 查询离线用户userId集合
     *
     * @param userIdList 用户id集合
     * @return
     */
    @Select("<script>SELECT DISTINCT user_id FROM ucb_user_device m WHERE is_invalid = 1 AND NOT EXISTS (SELECT 1 FROM (SELECT DISTINCT USER_ID FROM ucb_user_device WHERE is_invalid = 0) n WHERE n.user_id = m.user_id)" +
            "<if test='null != userIdList' >" +
            " AND user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach></if>" +
            "</script>")
    List<String> getOffLineUserId(@Param("userIdList") List<String> userIdList);

    /**
     * 查询符合条件的用户设备id集合
     *
     * @param userIdList 用户id集合
     * @param isInvalid  是否仅查询在线设备
     * @return 设备id集合
     */
    @Select("<script>SELECT DISTINCT id FROM ucb_user_device WHERE is_invalid = #{isInvalid} " +
            "<if test='null != userIdList' >" +
            " AND user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach></if>" +
            "</script>")
    List<String> getDeviceId(@Param("userIdList") List<String> userIdList, @Param("isInvalid") Boolean isInvalid);

    /**
     * 查询符合条件的用户设备id集合
     *
     * @param userIdList 用户id集合
     * @return
     */
    @Select("<script>SELECT * FROM ucb_user_device a JOIN (SELECT MAX(update_time) AS update_time,user_id FROM ucb_user_device WHERE " +
            " user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach>" +
            " GROUP BY user_id) b ON a.user_id = b.user_id AND a.update_time = b.update_time " +
            "</script>")
    List<Device> getAllDeviceId(@Param("userIdList") List<String> userIdList);


    /**
     * 根据userId集合或tag集合获取无重复的userId集合
     *
     * @param userIdList
     * @param tagList
     * @return
     */
    @Select("<script>SELECT DISTINCT user_id FROM ucb_user_tag WHERE 1=1 " +
            "<if test='null != userIdList and null != tagList' >" +
            " AND (user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach>" +
            " OR tag in " +
            "<foreach collection = \"tagList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach>) </if>" +

            "<if test='null == userIdList and null != tagList' >" +
            " AND tag in " +
            "<foreach collection = \"tagList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach> </if>" +

            "<if test='null != userIdList and null == tagList' >" +
            " AND user_id in <foreach collection = \"userIdList\" item = \"item\" index = \"index\" open=\"(\" close=\")\" separator = \",\">" +
            "#{item}" +
            "</foreach> </if>" +
            "</script>")
    List<String> getNoRepeatUserId(@Param("userIdList") List<String> userIdList, @Param("tagList") List<String> tagList);

    /**
     * 查询所有用户集合
     *
     * @return
     */
    @Select("SELECT DISTINCT user_id FROM ucb_user_device ")
    List<String> getAllDeviceUser();
}
