/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/*
Root of API, should have documentation on where to find the other resources.
 */
@Path("/")
public class RestIndex {
    @javax.ws.rs.core.Context public static ServletContext servletContext;

    /*
    The "GET" annotation indicates this method will respond to HTTP Get requests.
    The "Produces" annotation indicates the MIME response the method will return.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "<html><title>DSpace REST</title>" +
                "<body><h1>DSpace REST API</h1>" +
                "<h2>Communities</h2>"+
                "<dl>"+
                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/communities</dt>"+
                  "<dd>List all comunities. <a href='" + servletContext.getContextPath() + "/communities'>Demo</a></dd>"+

                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/communities/{id}</dt>"+
                  "<dd>Get details of community with id : {id}. <a href='" + servletContext.getContextPath() + "/communities/1'>Demo get details of community \"1\"</a></dd>"+
                "</dl>"+


                "<h2>Collections</h2>"+
                "<dl>"+
                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/collections</dt>"+
                  "<dd>List all collections. <a href='" + servletContext.getContextPath() + "/collections'>Demo</a></dd>"+

                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/collections/{id}</dt>"+
                  "<dd>Get details of collections with id : {id}. <a href='" + servletContext.getContextPath() + "/collections/1'>Demo get details of collection \"1\"</a></dd>"+
                "</dl>"+


                "<h2>Items</h2>"+
                "<dl>"+
                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/items</dt>"+
                  "<dd>List all items. <a href='" + servletContext.getContextPath() + "/items'>Demo</a></dd>"+

                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/items/{id}</dt>"+
                  "<dd>Get details of items with id : {id}. <a href='" + servletContext.getContextPath() + "/items/1'>Demo get details of item \"1\"</a></dd>"+
                "</dl>"+

                "<h2>Bitstreams</h2>"+
                "<dl>"+
                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/bitstreams</dt>"+
                  "<dd>List all bitstreams. <a href='" + servletContext.getContextPath() + "/bitstreams'>Demo</a></dd>"+

                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/bitstreams/{id}</dt>"+
                  "<dd>Get details of bitstream with id : {id}. <a href='" + servletContext.getContextPath() + "/bitstreams/1'>Demo get details of bitstream \"1\"</a></dd>"+

                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/bitstreams/{id}/retrieve</dt>"+
                  "<dd>Get details of bitstream with id : {id}. <a href='" + servletContext.getContextPath() + "/bitstreams/1/retrieve'>Download bitstream \"1\"</a></dd>"+
                "</dl>"+

                "<h2>Search Api</h2>"+
                "<dl>"+
                  "<dt>Method: GET URL: "+ servletContext.getContextPath() + "/search/item Possible Query Params : fields=[csv(;) list of fields to search for], expand=[csv(;) list of fields to expand (default:all)], limit=[nb of results to return (default:10)] </dt>"+
                  "<dd>Search for items </dd>"+
                "</dl>"+
                  
                "</body></html> ";
    }
}
