package com.example.myrealtripwithhyunndy.rsshelper

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RSSFeedResponse constructor(

    @field:Element(name="channel")
    var channel : RSSChannelResponse? = null
)

@Root(name = "channel", strict = false)
data class RSSChannelResponse constructor(

    /*
    @field:Element(name="title")
    var title : String? = "",
     */

    @field:ElementList(entry = "item", inline = true, required = false)
    var items : List<RSSitemResponse>? = null

)

@Root(name = "item", strict = false)
data class RSSitemResponse constructor (
    @field:Element(name="title", required = false)
    var title : String? = "",

    @field:Element(name="link", required = false)
    var link : String? = ""

    /*
    @field:Element(name="description", required = false)
    var description : String? =""
     */
)