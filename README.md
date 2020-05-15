# FastJavaTools
生成精简的层代码，以Map交互，铲掉pojo，保留少量dto



基本配置:
package = com.xxx.yyy
domainName = order
server = 127.0.0.1:8080/be

生成以下层:
com.xxx.yyy.web.controller.OrderController
com.xxx.yyy.service.impl.OrderService
com.xxx.yyy.dao.mapper.custom.OrderMapper
com.xxx.yyy.commons.consts.TableColumns  <可忽略>

依赖(也可忽略):
com.xxx.yyy.commons.models.dto.table.ChildrenColumn
com.xxx.yyy.commons.models.dto.table.Column
com.xxx.yyy.commons.models.dto.PageInfo<T>
com.xxx.yyy.commons.models.dto.ResponseData
com.xxx.yyy.commons.utils.MapUtils
com.xxx.yyy.commons.enums.ReturnCode
com.xxx.yyy.commons.consts.TableColumns
以及PageHelper插件


<<会生成于当前目录下，生成后开发者需要手工copy需要的文件于对应的目录下面。>>



