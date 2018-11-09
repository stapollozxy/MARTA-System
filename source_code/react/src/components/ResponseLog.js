import { Table } from 'antd';
import React from 'react';
import {ResponseLogData} from './ResponseLogData';
export class ResponseLog extends React.Component {
    columns = [{
        title: 'Num',
        dataIndex: 'number',
    }, {
        title: 'Content',
        dataIndex: 'content',
    }];

    render(){
        return (
            <div>
                <h4>Response List:</h4>
                <Table columns={this.columns} dataSource={ResponseLogData.getData()} size="small" />
            </div>
        );
    }
}