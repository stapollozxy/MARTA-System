import React from 'react';
import $ from 'jquery';
import { Form, Icon, Input, Button, message } from 'antd';
import { API_ROOT } from '../constants';
import { Link } from 'react-router-dom';
import {Log} from './Log';
import {LogData} from './LogData';
import {ResponseLogData} from './ResponseLogData';

const FormItem = Form.Item;

class UploadForm extends React.Component {
    constructor(){
        super();
        this.state ={
            reportData: ''
        };
    }

    state = {
        confirmDirty: false,
        autoCompleteResult: [],
        reportData: 'sd',
    };


    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                var d = LogData.getData();
                var rld = ResponseLogData.getData();
                var temp = {number: d.length, content:"upload from database " + values.csvfile};
                d.push(temp);
                LogData.setData(d);
                this.props.transferMsg(d, rld);
                var parent = this;
                this.props.transferMsg(d);
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: "upload_real_data," + values.csvfile
                }).then(function(response) {
                    message.success(response == "invalid input" ? "invalid file" : "successfully uploaded");
                    var tmp = {number: rld.length, content:response};
                    rld.push(tmp);
                    //this.props.transferMsg(d, rld).bind(window);
                    parent.props.transferMsg(d, rld);
                }, function(response) {
                    message.error(response.responseText);
                }).catch(function(error) {
                    message.error(error);
                });
                //this.setState({reportData : parent.state.reportData});
            }
        });
    }

    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 8 },
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 16 },
            },
        };

        const tailFormItemLayout = {
            wrapperCol: {
                xs: {
                    span: 14,
                    offset: 0,
                },
                sm: {
                    span: 12,
                    offset: 4,
                },
            },
        };

        return (
            <Form onSubmit={this.handleSubmit} className="singlesim-form">

                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Upload from Database</Button>
                </FormItem>
                <p>{this.state.reportData}</p>

                <FormItem
                    {...formItemLayout}
                    label="CSV file"
                >
                    {getFieldDecorator('csvfile', {
                        rules: [{ required: true, message: 'Please input your CSV file!', whitespace: true }],
                    })(
                        <Input defaultValue={"apcdata_week"}/>
                    )}
                </FormItem>
                <p>{"Example: apcdata_week"}</p>
            </Form>


        );
    }
}

export const UploadDatabase = Form.create()(UploadForm);