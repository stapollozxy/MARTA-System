import { Table } from 'antd';
import React from 'react';
import {LogData} from './LogData';
export class Log extends React.Component {
 columns = [{
    title: 'Number',
     dataIndex: 'number',
 }, {
    title: 'Content',
    dataIndex: 'content',
}];

    render(){
        return (
        <div>
            <h4>Command List:</h4>
            <Table columns={this.columns} dataSource={LogData.getData()} size="small" />
        </div>
        );
    }
}
