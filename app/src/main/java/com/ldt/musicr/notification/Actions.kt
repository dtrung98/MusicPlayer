package com.ldt.musicr.notification
/**
 * Created by dtrung98
 */
typealias ActionResponderType<T> = (Action<T>) -> Action<Any>?

/**
 * [ActionResponder] kết hợp [Action] có thể được sử dụng để lắng nghe event, thay thế listener truyền thống.
 *
 * Cách sử dụng:
 * + Class cần truyền phát event sẽ keep instance [ActionResponder] như listener truyền thống.
 * + Class cần lắng nghe event implement [ActionResponder], với hàm **ActionResponder#invoke()** nhận param [Action].
 * Các [Action] khác nhau được define với [Action.name] khác nhau.
 *
 * Tại sao:
 * + Class cần listen event chỉ implement 1 hàm duy nhất, xử lý khác nhau với các Action khác nhau.
 * + Class cần listen nhiều event từ các class khác nhau chỉ cần implement ActionResponder, không cần define các listener khác nhau.
 * + Truyền phát chuyền từ trong ra ngoài giữa các [ActionResponder], nếu 1 ActionResponder không handle thì có thể đá cho [ActionResponder] bên ngoài handle.
 * Ví dụ: ViewHolder đá event cho Adapter, adapter đá lại event cho ZView.
 *
 *
 * ```
 * object ActionConstants {
 *     const val ADAPTER_HANDLES_THIS_EVENT = "ADAPTER_HANDLES_ON_CLICK"
 *     const val ZVIEW_HANDLES_THIS_EVENT = "ZVIEW_HANDLES_THIS_EVENT"
 * }

 *  class Adapter : ActionResponder{
 *    var actionResponder: ActionResponder? = null
 *
 *    override fun invoke(action: Action<Any>): Action<Any>? {
 *        return when(action.name) {
 *                ActionConstants.ADAPTER_HANDLES_THIS_EVENT -> {
 *                        // do something
 *                        null
 *                        }
 *                // đá cho zview
 *                else -> actionResponder?.invoke(action)
 *        }
 *    }
 *
 *     class ViewHolder(val actionResponder: ActionResponder? = null) {
 *         fun onClick() {
 *             actionResponder?.invoke(Action(ActionConstants.ADAPTER_HANDLES_THIS_EVENT))
 *         }
 *
 *         fun onLongClick() {
 *             actionResponder?.invoke(Action(ActionConstants.ZVIEW_HANDLES_THIS_EVENT))
 *         }
 *     }
 *   }
 *
 *   class ZView: ActionResponder {
 *      val adapter = Adapter()
 *         fun onCreate() {
 *             adapter.actionResponder = this
 *         }
 *
 *         override fun invoke(action: Action<Any>): Action<Any>? {
 *             return when(action.name) {
 *                    ActionConstants.ZVIEW_HANDLES_THIS_EVENT -> {
 *                         Action(action.name, true)
 *                    }
 *                    else -> null
 *             }
 *         }
 *   }
 * ```
 */
interface ActionResponder : ActionResponderType<Any>
open class Action<T>(val name: String, val data: T? = null, val subData: Any? = null, val subSubData: Any? = null)
fun ActionResponder.invoke(name: String, data: Any? = null, subData: Any? = null, subSubData: Any? = null) = invoke(Action(name, data, subData, subSubData))