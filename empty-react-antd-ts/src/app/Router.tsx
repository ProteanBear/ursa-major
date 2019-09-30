import React from 'react';
import {HashRouter as Router,Route, Switch} from 'react-router-dom';

/**
 * 顶层路由：登录、非登录组、需登录组
 */
export default class AppRouter extends React.Component{
    render(){
        return (
            <Router>
                <Switch>
                </Switch>
            </Router>
        );
    }
}